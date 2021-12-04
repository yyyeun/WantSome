package smu.project_wantsome;

public class MemberInfo {
    private String name;
    private String address;
    private String chat;

    public MemberInfo(String name, String address, String chat) {
        this.name = name;
        this.address = address;
        this.chat = chat;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return this.address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getChat() {
        return this.chat;
    }
    public void setChat(String chat) {
        this.chat = chat;
    }

}