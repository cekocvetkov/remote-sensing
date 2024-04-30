package org.zhvtsv.service.stac.dto;

public class STACItemPreview {
    private String id;
    private String collection;
    private String thumbnailUrl;
    private String downloadUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    @Override
    public String toString() {
        return "STACItemPreview{" +
                "id='" + id + '\'' +
                ", thumbnailUrl='" + thumbnailUrl + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", collection='" + collection + '\'' +
                '}';
    }
}
