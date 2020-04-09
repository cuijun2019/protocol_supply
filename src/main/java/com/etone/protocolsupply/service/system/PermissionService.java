package com.etone.protocolsupply.service.system;


import com.etone.protocolsupply.model.entity.user.Permissions;
import com.etone.protocolsupply.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional(rollbackFor = Exception.class)
@Service
public class PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    public List<Map<String,Object>> getAllPermission() {
        //所有权限
        List<Permissions> permissionsList = permissionRepository.findAll();
        //要返回的结果集
        List<Map<String,Object>> returnMap = new ArrayList<>();

        //处理数据
        proPermissions(permissionsList, returnMap);

        return returnMap;
    }

    private void proPermissions(List<Permissions> permissionsList, List<Map<String, Object>> returnMap) {
        HashMap<String, Object> firstPageMap = new HashMap<>();
        ArrayList<Object> firstPageList = new ArrayList<>();

        HashMap<String, Object> projectMap = new HashMap<>();
        ArrayList<Object> projectList = new ArrayList<>();

        HashMap<String, Object> goodsMap = new HashMap<>();
        ArrayList<Object> goodsList = new ArrayList<>();

        HashMap<String, Object> pricesMap = new HashMap<>();
        ArrayList<Object> pricesList = new ArrayList<>();

        HashMap<String, Object> supplierMap = new HashMap<>();
        ArrayList<Object> supplierList = new ArrayList<>();

        HashMap<String, Object> agentMap = new HashMap<>();
        ArrayList<Object> agentList = new ArrayList<>();

        HashMap<String, Object> bidsMap = new HashMap<>();
        ArrayList<Object> bidsList = new ArrayList<>();

        HashMap<String, Object> contractMap = new HashMap<>();
        ArrayList<Object> contractList = new ArrayList<>();

        HashMap<String, Object> templateMap = new HashMap<>();
        ArrayList<Object> templateList = new ArrayList<>();

        HashMap<String, Object> systemMap = new HashMap<>();
        ArrayList<Object> systemList = new ArrayList<>();


        //数据处理
        for (int i = 0; i < permissionsList.size(); i++) {
            if(1==permissionsList.get(i).getPermId()){
                firstPageMap.put("permission",permissionsList.get(i));
            }else if(14==permissionsList.get(i).getPermId()){
                projectMap.put("permission",permissionsList.get(i));
            }else if(22==permissionsList.get(i).getPermId()){
                goodsMap.put("permission",permissionsList.get(i));
            }else if(31==permissionsList.get(i).getPermId()){
                pricesMap.put("permission",permissionsList.get(i));
            }else if(37==permissionsList.get(i).getPermId()){
                supplierMap.put("permission",permissionsList.get(i));
            }else if(47==permissionsList.get(i).getPermId()){
                agentMap.put("permission",permissionsList.get(i));
            }else if(57==permissionsList.get(i).getPermId()){
                bidsMap.put("permission",permissionsList.get(i));
            }else if(61==permissionsList.get(i).getPermId()){
                contractMap.put("permission",permissionsList.get(i));
            }else if(65==permissionsList.get(i).getPermId()){
                templateMap.put("permission",permissionsList.get(i));
            }else if(81==permissionsList.get(i).getPermId()){
                systemMap.put("permission",permissionsList.get(i));
            }



            if(1==permissionsList.get(i).getParentPermId()){
                firstPageList.add(permissionsList.get(i));
                firstPageMap.put("list",firstPageList);
            }else if(14==permissionsList.get(i).getParentPermId()){
                projectList.add(permissionsList.get(i));
                projectMap.put("list",projectList);
            }else if(22==permissionsList.get(i).getParentPermId()){
                goodsList.add(permissionsList.get(i));
                goodsMap.put("list",goodsList);
            }else if(31==permissionsList.get(i).getParentPermId()){
                pricesList.add(permissionsList.get(i));
                pricesMap.put("list",pricesList);
            }else if(37==permissionsList.get(i).getParentPermId()){
                supplierList.add(permissionsList.get(i));
                supplierMap.put("list",supplierList);
            }else if(47==permissionsList.get(i).getParentPermId()){
                agentList.add(permissionsList.get(i));
                agentMap.put("list",agentList);
            }else if(57==permissionsList.get(i).getParentPermId()){
                bidsList.add(permissionsList.get(i));
                bidsMap.put("list",bidsList);
            }else if(61==permissionsList.get(i).getParentPermId()){
                contractList.add(permissionsList.get(i));
                contractMap.put("list",contractList);
            }else if(65==permissionsList.get(i).getParentPermId()){
                templateList.add(permissionsList.get(i));
                templateMap.put("list",templateList);
            }else if(81==permissionsList.get(i).getParentPermId()){
                systemList.add(permissionsList.get(i));
                systemMap.put("list",systemList);
            }
        }
        if(systemMap.size()>0){
            returnMap.add(systemMap);
        }
        if(templateMap.size()>0){
            returnMap.add(templateMap);
        }
        if(contractMap.size()>0){
            returnMap.add(contractMap);
        }
        if(bidsMap.size()>0){
            returnMap.add(bidsMap);
        }
        if(agentMap.size()>0){
            returnMap.add(agentMap);
        }
        if(supplierMap.size()>0){
            returnMap.add(supplierMap);
        }
        if(pricesMap.size()>0){
            returnMap.add(pricesMap);
        }
        if(goodsMap.size()>0){
            returnMap.add(goodsMap);
        }
        if(projectMap.size()>0){
            returnMap.add(projectMap);
        }
        if(firstPageMap.size()>0){
            returnMap.add(firstPageMap);
        }
    }

    public List<Permissions> getThirdPermissionBySecondPermissionId(String permissionId) {
        return permissionRepository.findByPermissionId(Long.parseLong(permissionId));
    }

    //根据角色id查找角色权限
    public List<Map<String, Object>> getPermissionByRoleId(long roleId) {
        List<Permissions> permissionsList = permissionRepository.getPermissionByRoleId(roleId);

        //要返回的结果集
        List<Map<String,Object>> returnMap = new ArrayList<>();

        proPermissions(permissionsList,returnMap);

        //调用公共的处理权限的方法
        return returnMap;
    }

    //根据角色id查询该角色所拥有的权限(三级权限)
    public List<Permissions> getThirdPermissionByRoleId(String roleId, String secondPermissionId) {
        //先查询改用户的所有权限
        List<Permissions> permissionsList = permissionRepository.getPermissionByRoleId(Long.parseLong(roleId));
        ArrayList<Permissions> returnList = new ArrayList<>();

        for (int i = 0; i < permissionsList.size(); i++) {
            if(secondPermissionId.equals(permissionsList.get(i).getParentPermId().toString())){
                returnList.add(permissionsList.get(i));
            }
        }
        return returnList;
    }
}
