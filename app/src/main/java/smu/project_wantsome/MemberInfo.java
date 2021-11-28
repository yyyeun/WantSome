package smu.project_wantsome;

public class MemberInfo {
    private String name;
    private String address;
    private String chat;
    private String photoUri;

    public MemberInfo(String name, String address, String chat, String photoUri) {
        this.name = name;
        this.address = address;
        this.chat = chat;
        this.photoUri = photoUri;
    }

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

    public String getPhotoUri() { return this.photoUri;}
    public void setPhotoUri(String photoUri) {this.photoUri = photoUri;}
}