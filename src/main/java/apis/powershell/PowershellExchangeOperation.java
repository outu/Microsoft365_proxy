package apis.powershell;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import common.FileOperation;

import java.io.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PowershellExchangeOperation {
    private Map<String, String> authParameters;

    public PowershellExchangeOperation(Map<String, String> exchangeAuthParameters){
        authParameters = exchangeAuthParameters;
    }

    public String getUserInfo() throws IOException {
        String userInfo = "";

        String getUsersScript = String.format(
                PowershellScriptTemplate.authExchange,
                authParameters.get("password"),
                authParameters.get("username"),
                authParameters.get("protocol"),
                authParameters.get("domain")
        ) + PowershellScriptTemplate.getExchangeMailBox;

        String getUsersScriptFilePath = FileOperation.writeToTmpFile("getUsers.ps1", getUsersScript);
        BufferedReader stdout = runPowerShellScript(getUsersScriptFilePath);

        if (stdout == null){
            return userInfo;
        } else {
            String readLine = "";
            boolean needAnalyze = false;
            List<String> userInfoList = new ArrayList<>();

            while ((readLine = stdout.readLine()) != null) {
                if (needAnalyze && !readLine.equals("")){
                    String newReadLine = readLine.trim();
                    int lastPosition = newReadLine.lastIndexOf(" ");
                    String oneUserName = newReadLine.substring(0, lastPosition);
                    String oneUserMailBox = newReadLine.substring(lastPosition, newReadLine.length());
                    JsonObject oneUserInfo = new JsonObject();
                    oneUserInfo.addProperty("username", oneUserName.trim());
                    oneUserInfo.addProperty("mail", oneUserMailBox.trim());
                    userInfoList.add(oneUserInfo.toString());
                }

                if (readLine.contains("-----------   ------------------")){
                    needAnalyze = true;
                }
            }

            stdout.close();
            FileOperation.deleteTmpFile(getUsersScriptFilePath);
            Gson gson = new Gson();
            userInfo = gson.toJson(userInfoList);

            return userInfo;
        }
    }


    /**
     * 执行powershell脚本需要解析输出结果
     * @param scriptPath
     * @return
     */
    private BufferedReader runPowerShellScript(String scriptPath) {
        try {
            String command = "powershell.exe " + scriptPath;

            Process powerShellProcess = Runtime.getRuntime().exec(command);
            powerShellProcess.getOutputStream().close();

            return new BufferedReader(new InputStreamReader(powerShellProcess.getInputStream()));
        } catch (IOException e) {
            return null;
        }
    }

}
