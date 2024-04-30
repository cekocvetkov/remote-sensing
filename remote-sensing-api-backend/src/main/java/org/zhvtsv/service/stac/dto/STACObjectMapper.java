package org.zhvtsv.service.stac.dto;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class STACObjectMapper {

    public static List<STACItemPreview> getStacItemsPreview(List<String> jsonBodyStrings) {
        List<STACItemPreview> stacItemPreviewList = new ArrayList<>();

        for (String jsonBodyString : jsonBodyStrings) {
            try {
                JSONObject json = new JSONObject(jsonBodyString);
            } catch (Exception ex) {
                JSONArray jsonArray = new JSONArray(jsonBodyString);
                for (int i = 0; i < jsonArray.length(); i++) {
                    STACItemPreview stacItemPreview = getStacItemPreview(jsonArray.getJSONObject(i));
                    stacItemPreviewList.add(stacItemPreview);
                }
                continue;
            }
            JSONObject json = new JSONObject(jsonBodyString);
            if (json.optJSONArray("features") == null) {

                STACItemPreview stacItemPreview = getStacItemPreview(json);
                stacItemPreviewList.add(stacItemPreview);
                continue;
            }
            JSONArray featuresJsonArray = json.getJSONArray("features");
            for (int i = 0; i < featuresJsonArray.length(); i++) {
                JSONObject featureJSON = featuresJsonArray.getJSONObject(i);
                STACItemPreview stacItemPreview = getStacItemPreview(featureJSON);
                stacItemPreviewList.add(stacItemPreview);
            }
        }
        return stacItemPreviewList;
    }

    public static STACItemPreview getStacItemPreview(JSONObject jsonBody) {
        STACItemPreview stacItemPreview = new STACItemPreview();
        String id = jsonBody.getString("id");
        String collection = jsonBody.getString("collection");
        String downloadUrl = "";

        if (jsonBody.getJSONObject("assets").optJSONObject("image") != null) { //Planet Computer
            downloadUrl = jsonBody.getJSONObject("assets").getJSONObject("image").getString("href");
        } else if (jsonBody.getJSONObject("assets").optJSONObject("visual") != null) {
            downloadUrl = jsonBody.getJSONObject("assets").getJSONObject("visual").getString("href");
        } else {
            downloadUrl = jsonBody.getJSONObject("assets").getJSONObject("asset").getString("href");
        }
        String thumbnailUrl = "";
        if (jsonBody.getJSONObject("assets").optJSONObject("thumbnail") != null) {
            thumbnailUrl = jsonBody.getJSONObject("assets").getJSONObject("thumbnail").getString("href");
        }

        stacItemPreview.setId(id);
        stacItemPreview.setCollection(collection);
        stacItemPreview.setThumbnailUrl(thumbnailUrl);
        stacItemPreview.setDownloadUrl(downloadUrl);

        return stacItemPreview;
    }
}
