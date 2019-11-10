package cc.joyreactor.data;

public class Image {

    private int width;

    private int height;

    private String ref;

    public Image(int width, int height, String ref) {
        this.width = width;
        this.height = height;
        this.ref = ref;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }
}
