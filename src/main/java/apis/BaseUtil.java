package apis;

public class BaseUtil {
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