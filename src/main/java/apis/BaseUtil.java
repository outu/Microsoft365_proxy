package apis;

public class BaseUtil {
    public enum ApiTypeEnum {
        EWSAPI(0),
        GRAPHAPI(1);
        private int apiType = 0;

        private ApiTypeEnum(int value) {
            apiType = value;
        }


        private int getCode() {
            return apiType;
        }

        public static ApiTypeEnum getApiEnumByApiType(int apiType) {
            for (ApiTypeEnum apiEnum : ApiTypeEnum.values()) {
                if (apiEnum.getCode() == apiType) {
                    return apiEnum;
                }
            }

            return null;
        }

    }


    public enum RegionEnum{
        GLOBALCLOUD(0),
        CHINACLOUD(1),
        LOCAL(100);

        private int region = 0;
        private RegionEnum(int value){
            region = value;
        }

        private int getCode(){
            return region;
        }

        public static RegionEnum getRegionEnumByRegion(int region){
            for(RegionEnum regionEnum : RegionEnum.values()){
                if(regionEnum.getCode() == region){
                    return regionEnum;
                }
            }

            return null;
        }
    }



}