package org.zhvtsv.service.sentinel;

public class JsonRequestPayloadSampleData {
    public static String JSON_EVALSCRIPT = "//VERSION=3\n\n" +
            "function setup() {\n" +
            "  return {\n" +
            "    input: [\"B02\", \"B03\", \"B04\"],\n" +
            "    output: { bands: 3 }\n" +
            "  };\n" +
            "}\n\n" +
            "function evaluatePixel(sample) {\n" +
            "  return [2.5 * sample.B04, 2.5 * sample.B03, 2.5 * sample.B02];\n" +
            "}";
    public static String JSON_BODY_SENTINEL_HUB = "{\n" +
            "  \"input\": {\n" +
            "    \"bounds\": {\n" +
            "      \"bbox\": [\n" +
            "        25.245172,\n" +
            "        43.520547,\n" +
            "        25.36618,\n" +
            "        43.587106\n" +
            "      ]\n" +
            "    },\n" +
            "    \"data\": [\n" +
            "      {\n" +
            "        \"dataFilter\": {\n" +
            "          \"timeRange\": {\n" +
            "            \"from\": \"2023-09-22T00:00:00Z\",\n" +
            "            \"to\": \"2023-10-22T23:59:59Z\"\n" +
            "          }\n" +
            "        },\n" +
            "        \"type\": \"sentinel-2-l2a\"\n" +
            "      }\n" +
            "    ]\n" +
            "  },\n" +
            "  \"output\": {\n" +
            "    \"width\": 512,\n" +
            "    \"height\": 388.372,\n" +
            "    \"responses\": [\n" +
            "      {\n" +
            "        \"identifier\": \"default\",\n" +
            "        \"format\": {\n" +
            "          \"type\": \"image/tiff\"\n" +
            "        }\n" +
            "      }\n" +
            "    ]\n" +
            "  },\n" +
            "  \"evalscript\": \"//VERSION=3\\n\\nfunction setup() {\\n  return {\\n    input: [\\\"B02\\\", \\\"B03\\\", \\\"B04\\\"],\\n    output: { bands: 3 }\\n  };\\n}\\n\\nfunction evaluatePixel(sample) {\\n  return [2.5 * sample.B04, 2.5 * sample.B03, 2.5 * sample.B02];\\n}\"\n" +
            "}";
}
