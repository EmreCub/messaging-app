import uuid
import random
from locust import HttpUser, TaskSet, task, between

class MessagingTasks(TaskSet):
    def on_start(self):
        pw = "LocustPass123!"
        self.email = f"{uuid.uuid4().hex[:8]}@load.test"
        # register
        r = self.client.post(
            "/api/users/register",
            json={
                "firstName": "LT",
                "lastName":  uuid.uuid4().hex[:4],
                "email":     self.email,
                "password":  pw
            },
            name="POST /api/users/register"
        )
        r.raise_for_status()
        # login
        r = self.client.post(
            "/api/users/login",
            json={"email": self.email, "password": pw},
            name="POST /api/users/login"
        )
        r.raise_for_status()
        data = r.json()
        self.user_id = data["userId"]
        self.client.headers.update({"Authorization": f"Bearer {data['token']}"})

    @task(4)
    def friend_flow(self):
        # search (always OK)
        self.client.get("/api/friends/search?query=test", name="GET /api/friends/search")

        # add (ignore any 4xx)
        peer = random.choice([self.user_id] + [str(uuid.uuid4())])
        with self.client.post(
            "/api/friends/add",
            json={"sender": self.user_id, "receiver": peer},
            name="POST /api/friends/add",
            catch_response=True,
        ) as resp:
            if resp.status_code < 500:
                resp.success()

        # view pending (ignore 404/400)
        with self.client.get(
            f"/api/friends?userId={self.user_id}",
            name="GET /api/friends",
            catch_response=True,
        ) as resp:
            if resp.status_code < 500:
                resp.success()

        # view all (ignore 404/400)
        with self.client.get(
            f"/api/friends/all?userId={self.user_id}",
            name="GET /api/friends/all",
            catch_response=True,
        ) as resp:
            if resp.status_code < 500:
                resp.success()

    @task(4)
    def messaging_flow(self):
        peer = random.choice([self.user_id] + [str(uuid.uuid4())])
        with self.client.post(
            "/api/messages/send",
            json={"senderId": self.user_id, "receiverId": peer, "content": "ping"},
            name="POST /api/messages/send",
            catch_response=True,
        ) as resp:
            if resp.status_code < 500:
                resp.success()

        self.client.get(
            f"/api/messages?user1={self.user_id}&user2={peer}",
            name="GET /api/messages"
        )

    @task(3)
    def group_flow(self):
        gid = str(uuid.uuid4())
        self.client.post(
            "/api/groups/create",
            json={"name": f"grp-{gid[:4]}", "groupId": gid, "members": [self.user_id]},
            name="POST /api/groups/create"
        )
        self.client.get(f"/api/groups?userId={self.user_id}", name="GET /api/groups")

        for path, nm in [
            (f"/api/groups/{gid}/details",  "GET /api/groups/[id]/details"),
            (f"/api/groups/{gid}/members",  "GET /api/groups/[id]/members"),
            (f"/api/groups/{gid}/messages", "GET /api/groups/[id]/messages"),
        ]:
            self.client.get(path, name=nm)

        self.client.post(
            f"/api/groups/{gid}/send",
            json={"groupId": gid, "senderId": self.user_id, "content": "hello grp"},
            name="POST /api/groups/[id]/send"
        )

class MessagingUser(HttpUser):
    tasks = [MessagingTasks]
    wait_time = between(1, 2)