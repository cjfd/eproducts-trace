package com.zhonghui.demo.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Properties;


@Controller
public class IndexController {
    private  static String URL;
    private static String PRODUCER_ADDRESS;
    private static String DISTRIBUTOR_ADDRESS;
    private static String RETAILER_ADDRESS;
    private static  final String  CONTRACT_ABI = "[{\"constant\":true,\"inputs\":[{\"name\":\"traceNumber\",\"type\":\"uint256\"}],\"name\":\"getEproduct\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"},{\"name\":\"\",\"type\":\"string\"},{\"name\":\"\",\"type\":\"string\"},{\"name\":\"\",\"type\":\"string\"},{\"name\":\"\",\"type\":\"address\"},{\"name\":\"\",\"type\":\"uint8\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"traceNumber\",\"type\":\"uint256\"}],\"name\":\"getTraceInfo\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256[]\"},{\"name\":\"\",\"type\":\"string[]\"},{\"name\":\"\",\"type\":\"address[]\"},{\"name\":\"\",\"type\":\"uint8[]\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"traceNumber\",\"type\":\"uint256\"},{\"name\":\"traceName\",\"type\":\"string\"},{\"name\":\"quality\",\"type\":\"uint8\"}],\"name\":\"addTraceInfoByDistributor\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"getAllEproducts\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256[]\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"account\",\"type\":\"address\"}],\"name\":\"isRetailer\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[],\"name\":\"renounceDistributor\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"account\",\"type\":\"address\"}],\"name\":\"addDistributor\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"account\",\"type\":\"address\"}],\"name\":\"addRetailer\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"account\",\"type\":\"address\"}],\"name\":\"isDistributor\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"traceNumber\",\"type\":\"uint256\"},{\"name\":\"traceName\",\"type\":\"string\"},{\"name\":\"quality\",\"type\":\"uint8\"}],\"name\":\"addTraceInfoByRetailer\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[],\"name\":\"renounceRetailer\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"account\",\"type\":\"address\"}],\"name\":\"addProducer\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"account\",\"type\":\"address\"}],\"name\":\"isProducer\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[],\"name\":\"renounceProducer\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"traceNumber\",\"type\":\"uint256\"},{\"name\":\"traceName\",\"type\":\"string\"},{\"name\":\"quality\",\"type\":\"uint8\"}],\"name\":\"newEproducts\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"name\":\"producer\",\"type\":\"address\"},{\"name\":\"distributor\",\"type\":\"address\"},{\"name\":\"retailer\",\"type\":\"address\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"account\",\"type\":\"address\"}],\"name\":\"RetailerAdded\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"account\",\"type\":\"address\"}],\"name\":\"RetailerRemoved\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"account\",\"type\":\"address\"}],\"name\":\"DistributorAdded\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"account\",\"type\":\"address\"}],\"name\":\"DistributorRemoved\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"account\",\"type\":\"address\"}],\"name\":\"ProducerAdded\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"account\",\"type\":\"address\"}],\"name\":\"ProducerRemoved\",\"type\":\"event\"}]";
    private static String  CONTRACT_ADDRESS;
    private static String  CONTRACT_NAME = "Trace";
    public void loadConfig(){
        try{
            // read properties
            Properties properties = new Properties();
            File file = new File("conf.properties");
            FileInputStream fis = new FileInputStream(file);
            properties.load(fis);
            fis.close();

            // load properties
            URL = properties.getProperty("URL");
            System.out.println(URL);
            PRODUCER_ADDRESS = properties.getProperty("PRODUCER_ADDRESS");
            System.out.println(PRODUCER_ADDRESS);
            DISTRIBUTOR_ADDRESS = properties.getProperty("DISTRIBUTOR_ADDRESS");
            System.out.println(DISTRIBUTOR_ADDRESS);
            RETAILER_ADDRESS = properties.getProperty("RETAILER_ADDRESS");
            System.out.println(RETAILER_ADDRESS);
            CONTRACT_ADDRESS = properties.getProperty("CONTRACT_ADDRESS");
            System.out.println(CONTRACT_ADDRESS);

        }catch (Exception e){
           e.printStackTrace();
        }
    }
    public IndexController (){
        loadConfig();
    }

    @GetMapping("/index")
    public String  index(){
        return "index";
    }

    @ResponseBody
    @GetMapping( path = "/userinfo",produces = MediaType.APPLICATION_JSON_VALUE)
    public String userInfo(String userName){
        JSONObject _objPut = new JSONObject();
        if (userName.equals("producer")){
            _objPut.put("address",PRODUCER_ADDRESS);
        } else if (userName.equals("distributor")) {
            _objPut.put("address",DISTRIBUTOR_ADDRESS);
        } else if (userName.equals("retailer")) {
            _objPut.put("address",RETAILER_ADDRESS);
        } else {
            _objPut.put("error", "user not found");
        }
        return  _objPut.toJSONString();
    }

    private String httpPost(String url,String jsonStr){
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-type","application/json;charset=utf-8");
        StringEntity entity = new StringEntity(jsonStr,Charset.forName("UTF-8"));
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json");
        httpPost.setEntity(entity);
        CloseableHttpResponse httpResponse;
        String result = null;
        try {
            httpResponse =httpClient.execute(httpPost);
            result = EntityUtils.toString(httpResponse.getEntity(),  "UTF-8");
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;

    }

      private JSONArray get_eproducts_list(){

        JSONObject _objPut = new JSONObject();
        _objPut.put("contractName",CONTRACT_NAME);
        _objPut.put("contractAddress",CONTRACT_ADDRESS);
        _objPut.put("contractAbi",JSONArray.parseArray(CONTRACT_ABI));
        _objPut.put("user","");
        _objPut.put("funcName","getAllEproducts");

        String responseStr = httpPost(URL,_objPut.toJSONString());
        JSONArray responseJsonObj = JSON.parseArray(responseStr);
        return responseJsonObj;
      }


    private JSONArray get_trace(String traceNumber){

        JSONArray params = JSONArray.parseArray("[" + traceNumber + "]");
        JSONObject _objPut = new JSONObject();
        _objPut.put("contractName",CONTRACT_NAME);
        _objPut.put("contractAddress",CONTRACT_ADDRESS);
        _objPut.put("contractAbi",JSONArray.parseArray(CONTRACT_ABI));
        _objPut.put("user","");
        _objPut.put("funcName","getEproduct");
        _objPut.put("funcParam",params);


        String responseStr = httpPost(URL,_objPut.toJSONString());
        JSONArray eProduct = JSON.parseArray(responseStr);

        JSONObject _objPut2 = new JSONObject();
        _objPut2.put("contractName",CONTRACT_NAME);
        _objPut2.put("contractAddress",CONTRACT_ADDRESS);
        _objPut2.put("contractAbi",JSONArray.parseArray(CONTRACT_ABI));
        _objPut2.put("user","");
        _objPut2.put("funcName","getTraceInfo");
        _objPut2.put("funcParam",params);


        String responseStr2 = httpPost(URL,_objPut2.toJSONString());
        JSONArray traceInfoList = JSON.parseArray(responseStr2);
        JSONArray time_list = JSONObject.parseArray(traceInfoList.get(0).toString());
        JSONArray name_list = JSONObject.parseArray(traceInfoList.get(1).toString());
        JSONArray address_list = JSONObject.parseArray(traceInfoList.get(2).toString());
        JSONArray quality_list = JSONObject.parseArray(traceInfoList.get(3).toString());


        JSONArray _obj = new JSONArray();
        for (int i=0;i<time_list.size();i++){
            if (i ==0 ){
                JSONObject _objTemp = new JSONObject();
                _objTemp.put("traceNumber",traceNumber);
                _objTemp.put("name", eProduct.get(2));
                _objTemp.put("produce_time",eProduct.get(0));
                _objTemp.put("timestamp",time_list.get(i));
                _objTemp.put("from",name_list.get(i));
                _objTemp.put("quality",quality_list.get(i));
                _objTemp.put("from_address",address_list.get(i));
                _obj.add(_objTemp);
            }else {
                JSONObject _objTemp = new JSONObject();
                _objTemp.put("traceNumber",traceNumber);
                _objTemp.put("name", eProduct.get(2));
                _objTemp.put("produce_time",eProduct.get(0));
                _objTemp.put("timestamp",time_list.get(i));
                _objTemp.put("from",name_list.get(i-1));
                _objTemp.put("to",name_list.get(i));
                _objTemp.put("quality",quality_list.get(i));
                _objTemp.put("from_address",address_list.get(i-1));
                _objTemp.put("to_address",address_list.get(i));
                _obj.add(_objTemp);

            }
        }

        System.out.println(((_obj.getJSONObject(0)).get("timestamp")) instanceof Long);
        return _obj;
    }

    @ResponseBody
    @GetMapping( path = "/producing",produces = MediaType.APPLICATION_JSON_VALUE)
    public String get_producing(){
        JSONArray  num_list = get_eproducts_list();
        System.out.println(num_list);

       JSONArray num_list2 = JSONArray.parseArray(num_list.get(0).toString());
        JSONArray res_list = new JSONArray();
        for (int i=0;i< num_list2.size();i++){
            JSONArray trace = get_trace(num_list2.get(i).toString());
            if (trace.size() == 1){
                res_list.add(trace.get(0));
            }
        }
        return res_list.toJSONString();
    }

    @ResponseBody
    @PostMapping( path = "/produce",produces=MediaType.APPLICATION_JSON_VALUE)
    public String produce(@RequestBody JSONObject jsonParam){
       JSONObject _objPut  = new JSONObject();
       if (jsonParam == null){
           _objPut.put("error","invalid params");
           return _objPut.toJSONString();
        }

       Integer trace_number = Integer.valueOf(jsonParam.get("traceNumber").toString());
       String trace_name  = (String) jsonParam.get("traceName");
        String eproducts_name  = (String) jsonParam.get("eproductsName");
        Integer quality = Integer.valueOf(jsonParam.get("quality").toString());

        JSONArray params = JSONArray.parseArray("[\"" + eproducts_name + "\"," + trace_number + ",\""+ trace_name + "\"," +quality  +"]");

        JSONObject _obj = new JSONObject();
        _obj.put("contractName",CONTRACT_NAME);
        _obj.put("contractAddress",CONTRACT_ADDRESS);
        _obj.put("contractAbi",JSONArray.parseArray(CONTRACT_ABI));
        _obj.put("user",PRODUCER_ADDRESS);
        _obj.put("funcName","newEproducts");
        _obj.put("funcParam",params);

        String responseStr = httpPost(URL,_obj.toJSONString());
        System.out.println(responseStr);
        JSONObject responseJsonObject = JSON.parseObject(responseStr);
        String msg = responseJsonObject.getString("message");

        if(msg.equals("Success")){
            _objPut.put("ret",1);
            _objPut.put("msg",msg);
        } else{
            _objPut.put("ret",0);
            _objPut.put("msg",msg);
        }
        return _objPut.toJSONString();
    }

    @ResponseBody
    @GetMapping(path="/trace", produces=MediaType.APPLICATION_JSON_VALUE)
    public String trace(String traceNumber){
        JSONObject outPut = new JSONObject();
        if(Integer.parseInt(traceNumber)<=0){
            outPut.put("error","invalid parameter");
            return outPut.toJSONString();
        }
            List res =get_trace(traceNumber);
             JSONArray o=new JSONArray(res);
             return o.toJSONString();
    }

    @ResponseBody
    @PostMapping( path = "/addDistribution",produces=MediaType.APPLICATION_JSON_VALUE)
    public String addDistribution(@RequestBody JSONObject jsonParam){
        JSONObject _objPut  = new JSONObject();
        if (jsonParam == null){
            _objPut.put("error","invalid params");
            return _objPut.toJSONString();
        }
        Integer trace_number = Integer.valueOf(jsonParam.get("traceNumber").toString());
        String trace_name  = (String) jsonParam.get("traceName");
        Integer quality = Integer.valueOf(jsonParam.get("quality").toString());
        JSONArray params = JSONArray.parseArray("[" + trace_number + ",\"" +trace_name + "\"," + quality + "]");

        JSONObject _obj = new JSONObject();
        _obj.put("contractName",CONTRACT_NAME);
        _obj.put("contractAddress",CONTRACT_ADDRESS);
        _obj.put("contractAbi",JSONArray.parseArray(CONTRACT_ABI));
        _obj.put("user",DISTRIBUTOR_ADDRESS);
        _obj.put("funcName","addTraceInfoByDistributor");
        _obj.put("funcParam",params);

        String responseStr = httpPost(URL,_obj.toJSONString());
        System.out.println(responseStr);
        JSONObject responseJsonObject = JSON.parseObject(responseStr);
        String msg = responseJsonObject.getString("message");

        if(msg.equals("Success")){
            _objPut.put("ret",1);
            _objPut.put("msg",msg);
        } else{
            _objPut.put("ret",0);
            _objPut.put("msg",msg);
        }
        return _objPut.toJSONString();

    }
    @ResponseBody
    @GetMapping( path = "/distributing",produces = MediaType.APPLICATION_JSON_VALUE)
    public String get_distributing(){
        JSONArray  num_list = get_eproducts_list();
        System.out.println(num_list);

        JSONArray num_list2 = JSONObject.parseArray(num_list.get(0).toString());
        JSONArray res_list = new JSONArray();
        for (int i=0;i< num_list2.size();i++){
            List trace = get_trace(num_list2.get(i).toString());
            if (trace.size() == 2){
                res_list.add(trace.get(1));
            }
        }
        return res_list.toJSONString();
    }
    @ResponseBody
    @PostMapping( path = "/addretail",produces=MediaType.APPLICATION_JSON_VALUE)
    public String addRetail(@RequestBody JSONObject jsonParam){
        JSONObject _objPut  = new JSONObject();
        if (jsonParam == null){
            _objPut.put("error","invalid params");
            return _objPut.toJSONString();
        }
        Integer trace_number = Integer.valueOf(jsonParam.get("traceNumber").toString());
        String trace_name  = (String) jsonParam.get("traceName");
        Integer quality = Integer.valueOf(jsonParam.get("quality").toString());
        JSONArray params = JSONArray.parseArray("[" + trace_number + ",\"" +trace_name + "\"," + quality + "]");

        JSONObject _obj = new JSONObject();
        _obj.put("contractName",CONTRACT_NAME);
        _obj.put("contractAddress",CONTRACT_ADDRESS);
        _obj.put("contractAbi",JSONArray.parseArray(CONTRACT_ABI));
        _obj.put("user",DISTRIBUTOR_ADDRESS);
        _obj.put("funcName","addTraceInfoByRetailer");
        _obj.put("funcParam",params);

        String responseStr = httpPost(URL,_obj.toJSONString());
        System.out.println(responseStr);
        JSONObject responseJsonObject = JSON.parseObject(responseStr);
        String msg = responseJsonObject.getString("message");

        if(msg.equals("Success")){
            _objPut.put("ret",1);
            _objPut.put("msg",msg);
        } else{
            _objPut.put("ret",0);
            _objPut.put("msg",msg);
        }
        return _objPut.toJSONString();

    }
    @ResponseBody
    @GetMapping( path = "/retailing",produces = MediaType.APPLICATION_JSON_VALUE)
    public String get_retailing(){
        JSONArray  num_list = get_eproducts_list();
        System.out.println(num_list);

        JSONArray num_list2 = JSONArray.parseArray(num_list.get(0).toString());
        JSONArray res_list = new JSONArray();
        for (int i=0;i< num_list2.size();i++){
            JSONArray trace = get_trace(num_list2.get(i).toString());
            if (trace.size() == 3){
                res_list.add(trace.get(2));
            }
        }
        return res_list.toJSONString();
    }

    @ResponseBody
    @GetMapping( path = "/eproductslist",produces = MediaType.APPLICATION_JSON_VALUE)
    public String get_eproductslist(){
        JSONArray  num_list = get_eproducts_list();
        //System.out.println(num_list);

        JSONArray num_list2 = JSONArray.parseArray(num_list.get(0).toString());
        JSONArray res_list = new JSONArray();
        for (int i=0;i< num_list2.size();i++){
           String eproducts  = get_eproducts(num_list2.get(i).toString());
           res_list.add(eproducts);

        }
        return res_list.toJSONString();
    }

    private  String get_eproducts(String traceNumber){
        JSONArray params = JSONArray.parseArray("[" + traceNumber + "]");
        JSONObject _objPut = new JSONObject();
        _objPut.put("contractName",CONTRACT_NAME);
        _objPut.put("contractAddress",CONTRACT_ADDRESS);
        _objPut.put("contractAbi",JSONArray.parseArray(CONTRACT_ABI));
        _objPut.put("user","");
        _objPut.put("funcName","getEproduct");
        _objPut.put("funcParam",params);


        String responseStr = httpPost(URL,_objPut.toJSONString());
        JSONArray eProducts = JSON.parseArray(responseStr);

        JSONObject _obj = new JSONObject();
        _obj.put("timestamp", eProducts.get(0));
        _obj.put("produce", eProducts.get(1));
        _obj.put("name", eProducts.get(2));
        _obj.put("current", eProducts.get(3));
        _obj.put("address", eProducts.get(4));
        _obj.put("quality", eProducts.get(5));
        return _obj.toJSONString();
    }

    @ResponseBody
    @GetMapping( path = "/eproducts",produces = MediaType.APPLICATION_JSON_VALUE)
    public String eproducts (String traceNumber){
       // JSONObject _objPut  = new JSONObject();
        String res = get_eproducts(traceNumber);
        return res;
    }
    @ResponseBody
    @GetMapping( path = "/newtracelist",produces = MediaType.APPLICATION_JSON_VALUE)
    public String newtracelist(){
        JSONArray  num_list = get_eproducts_list();
        //System.out.println(num_list);

        JSONArray num_list2 = JSONArray.parseArray(num_list.get(0).toString());
        JSONArray res_list = new JSONArray();
        for (int i=0;i< num_list2.size();i++){
            List trace  = get_trace(num_list2.get(i).toString());
            res_list.add(trace.get(trace.size()-1));

        }
        return res_list.toJSONString();
    }

}








































