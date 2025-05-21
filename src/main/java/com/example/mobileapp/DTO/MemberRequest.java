package com.example.mobileapp.DTO;

public class MemberRequest {
    private String memberId;

    // Constructor
    public MemberRequest() {
    }

    public MemberRequest(String memberId) {
        this.memberId = memberId;
    }

    // Getter and Setter
    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }
}
