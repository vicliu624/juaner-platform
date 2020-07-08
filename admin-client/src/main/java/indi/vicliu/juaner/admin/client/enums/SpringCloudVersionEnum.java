package indi.vicliu.juaner.admin.client.enums;


public enum SpringCloudVersionEnum {

    SC_Finchley_RELEASE("2.0.0.RELEASE","Finchley.RELEASE","Spring Cloud Finchley.RELEASE"),
    SC_FINCHLEY_SR2("2.0.2.RELEASE","Finchley.SR2","Spring Cloud Finchley.SR2"),
    SC_FINCHLEY_SR3("2.0.3.RELEASE","Finchley.SR3","Spring Cloud Finchley.SR3"),
    SC_Greenwich_RELEASE("2.1.0.RELEASE","Greenwich.SR2","Spring Cloud Greenwich.SR2"),
    SC_Hoxton_RELEASE("2.2.0.RELEASE","Hoxton.SR4","Spring Cloud Hoxton.SR4");

    private String springCommonVersion;
    private String springcloudVersion;
    private String desc;

    SpringCloudVersionEnum(String springCommonVersion, String springcloudVersion, String desc) {
        this.springCommonVersion = springCommonVersion;
        this.springcloudVersion = springcloudVersion;
        this.desc = desc;
    }

    public  static String getScVersionByCommonVersion(String springCloudommonVersion){
        for (SpringCloudVersionEnum scVersionEnum : SpringCloudVersionEnum.values()) {
            if (scVersionEnum.springCommonVersion.equalsIgnoreCase(springCloudommonVersion)){
               return scVersionEnum.springcloudVersion;
            }
        }
        return null;
    }
}
