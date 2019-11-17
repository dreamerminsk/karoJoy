package cc.joyreactor.data;

public class Tag {

    private int id;

    private String tag;

    private String ref;

    private String ids;

    private byte[] avatar;

    private byte[] banner;

    public Tag(int id, String tag, String ref, String ids) {
        this.id = id;
        this.tag = tag;
        this.ref = ref;
        this.ids = ids;
    }

    public Tag(int id, String tag, String ref, String ids, byte[] avatar, byte[] banner) {
        this.id = id;
        this.tag = tag;
        this.ref = ref;
        this.ids = ids;
        this.avatar = avatar;
        this.banner = banner;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String toString() {
        return tag;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }

    public byte[] getBanner() {
        return banner;
    }

    public void setBanner(byte[] banner) {
        this.banner = banner;
    }
}
