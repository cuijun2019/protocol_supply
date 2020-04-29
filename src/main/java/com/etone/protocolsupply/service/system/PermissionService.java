package com.etone.protocolsupply.service.system;


import com.etone.protocolsupply.model.entity.user.Permissions;
import com.etone.protocolsupply.repository.user.PermissionRepository;
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
        //首页
        HashMap<String, Object> firstPageMap = new HashMap<>();
        ArrayList<Object> firstPageList = new ArrayList<>();
        HashMap<String, Object> firstPageMaptodo = new HashMap<>();
        ArrayList<Object> firstPageListtodo = new ArrayList<>();
        HashMap<String, Object> firstPageMapdone = new HashMap<>();
        ArrayList<Object> firstPageListdone = new ArrayList<>();
        HashMap<String, Object> firstPageMappending = new HashMap<>();
        ArrayList<Object> firstPageListpending = new ArrayList<>();
        HashMap<String, Object> firstPageMapread = new HashMap<>();
        ArrayList<Object> firstPageListread = new ArrayList<>();

        //项目管理
        HashMap<String, Object> projectMap = new HashMap<>();
        ArrayList<Object> projectList = new ArrayList<>();
        HashMap<String, Object> projectMapMyProject = new HashMap<>();
        ArrayList<Object> projectListMyProject = new ArrayList<>();

        //货物管理
        HashMap<String, Object> goodsMap = new HashMap<>();
        ArrayList<Object> goodsList = new ArrayList<>();
        HashMap<String, Object> goodsMapMyGoods = new HashMap<>();
        ArrayList<Object> goodsListMyGoods = new ArrayList<>();

        //询价管理
        HashMap<String, Object> pricesMap = new HashMap<>();
        ArrayList<Object> pricesList = new ArrayList<>();
        HashMap<String, Object> pricesMapMyPrice = new HashMap<>();
        ArrayList<Object> pricesListMyPrice = new ArrayList<>();

        //供应商管理
        HashMap<String, Object> supplierMap = new HashMap<>();
        ArrayList<Object> supplierList = new ArrayList<>();
        HashMap<String, Object> supplierMapMySupplier = new HashMap<>();
        ArrayList<Object> supplierListMySupplier = new ArrayList<>();
        HashMap<String, Object> supplierMapInfo = new HashMap<>();
        ArrayList<Object> supplierListInfo = new ArrayList<>();
        HashMap<String, Object> supplierMapUpdate = new HashMap<>();
        ArrayList<Object> supplierListUpdate = new ArrayList<>();
        HashMap<String, Object> supplierMapResetPWD = new HashMap<>();
        ArrayList<Object> supplierListResetPWD  = new ArrayList<>();

        //代理商管理
        HashMap<String, Object> agentMap = new HashMap<>();
        ArrayList<Object> agentList = new ArrayList<>();
        HashMap<String, Object> agentMapMyAgent = new HashMap<>();
        ArrayList<Object> agentListMyAgent = new ArrayList<>();
        HashMap<String, Object> agentMapInfo = new HashMap<>();
        ArrayList<Object> agentListInfo = new ArrayList<>();
        HashMap<String, Object> agentMapUpdate = new HashMap<>();
        ArrayList<Object> agentListUpdate = new ArrayList<>();
        HashMap<String, Object> agentMapResetPWD = new HashMap<>();
        ArrayList<Object> agentListResetPWD  = new ArrayList<>();

        //采购结果通知书
        HashMap<String, Object> purchaseResultsMap = new HashMap<>();
        ArrayList<Object> purchaseResultsList = new ArrayList<>();
        HashMap<String, Object> purchaseResultsMapMy = new HashMap<>();
        ArrayList<Object> purchaseResultsListMy = new ArrayList<>();


        //中标通知书
        HashMap<String, Object> bidsMap = new HashMap<>();
        ArrayList<Object> bidsList = new ArrayList<>();
        HashMap<String, Object> bidsMapMyBids = new HashMap<>();
        ArrayList<Object> bidsListMyBids = new ArrayList<>();

        //合同
        HashMap<String, Object> contractMap = new HashMap<>();
        ArrayList<Object> contractList = new ArrayList<>();
        HashMap<String, Object> contractMapMy = new HashMap<>();
        ArrayList<Object> contractListMy = new ArrayList<>();

        //模板管理
        HashMap<String, Object> templateMap = new HashMap<>();
        ArrayList<Object> templateList = new ArrayList<>();
        HashMap<String, Object> templateMapPurchase = new HashMap<>();
        ArrayList<Object> templateListPurchase = new ArrayList<>();
        HashMap<String, Object> templateMapBids = new HashMap<>();
        ArrayList<Object> templateListBids = new ArrayList<>();
        HashMap<String, Object> templateMapContract = new HashMap<>();
        ArrayList<Object> templateListContract = new ArrayList<>();

        //系统管理
        HashMap<String, Object> systemMap = new HashMap<>();
        ArrayList<Object> systemList = new ArrayList<>();
        HashMap<String, Object> systemMapUser = new HashMap<>();
        ArrayList<Object> systemListUser = new ArrayList<>();
        HashMap<String, Object> systemMapRole = new HashMap<>();
        ArrayList<Object> systemListRole = new ArrayList<>();


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
            }else if(53==permissionsList.get(i).getPermId()){
                purchaseResultsMap.put("permission",permissionsList.get(i));
            } else if(57==permissionsList.get(i).getPermId()){
                bidsMap.put("permission",permissionsList.get(i));
            }else if(61==permissionsList.get(i).getPermId()){
                contractMap.put("permission",permissionsList.get(i));
            }else if(65==permissionsList.get(i).getPermId()){
                templateMap.put("permission",permissionsList.get(i));
            }else if(81==permissionsList.get(i).getPermId()){
                systemMap.put("permission",permissionsList.get(i));
            }else if(2==permissionsList.get(i).getPermId()){
                firstPageMaptodo.put("permission",permissionsList.get(i));
            }else if(3==permissionsList.get(i).getPermId()){
                firstPageMapdone.put("permission",permissionsList.get(i));
            }else if(4==permissionsList.get(i).getPermId()){
                firstPageMappending.put("permission",permissionsList.get(i));
            }else if(5==permissionsList.get(i).getPermId()){
                firstPageMapread.put("permission",permissionsList.get(i));
            }else if(15==permissionsList.get(i).getPermId()){
                projectMapMyProject.put("permission",permissionsList.get(i));
            }else if(23==permissionsList.get(i).getPermId()){
                goodsMapMyGoods.put("permission",permissionsList.get(i));
            }else if(32==permissionsList.get(i).getPermId()){
                pricesMapMyPrice.put("permission",permissionsList.get(i));
            }else if(38==permissionsList.get(i).getPermId()){
                supplierMapMySupplier.put("permission",permissionsList.get(i));
            }else if(39==permissionsList.get(i).getPermId()){
                supplierMapInfo.put("permission",permissionsList.get(i));
            }else if(40==permissionsList.get(i).getPermId()){
                supplierMapUpdate.put("permission",permissionsList.get(i));
            }else if(41==permissionsList.get(i).getPermId()){
                supplierMapResetPWD.put("permission",permissionsList.get(i));
            }else if(48==permissionsList.get(i).getPermId()){
                agentMapMyAgent.put("permission",permissionsList.get(i));
            }else if(92==permissionsList.get(i).getPermId()){
                agentMapInfo.put("permission",permissionsList.get(i));
            }else if(93==permissionsList.get(i).getPermId()){
                agentMapUpdate.put("permission",permissionsList.get(i));
            }else if(94==permissionsList.get(i).getPermId()){
                agentMapResetPWD.put("permission",permissionsList.get(i));
            }else if(54==permissionsList.get(i).getPermId()){
                purchaseResultsMapMy.put("permission",permissionsList.get(i));
            }else if(58==permissionsList.get(i).getPermId()){
                bidsMapMyBids.put("permission",permissionsList.get(i));
            }else if(62==permissionsList.get(i).getPermId()){
                contractMapMy.put("permission",permissionsList.get(i));
            }else if(66==permissionsList.get(i).getPermId()){
                templateMapPurchase.put("permission",permissionsList.get(i));
            }else if(67==permissionsList.get(i).getPermId()){
                templateMapBids.put("permission",permissionsList.get(i));
            }else if(68==permissionsList.get(i).getPermId()){
                templateMapContract.put("permission",permissionsList.get(i));
            }else if(82==permissionsList.get(i).getPermId()){
                systemMapUser.put("permission",permissionsList.get(i));
            }else if(83==permissionsList.get(i).getPermId()){
                systemMapRole.put("permission",permissionsList.get(i));
            }



            if(1==permissionsList.get(i).getParentPermId()){
                //firstPageList.add(permissionsList.get(i));
                firstPageMap.put("list",firstPageList);
            }else if(14==permissionsList.get(i).getParentPermId()){
                //projectList.add(permissionsList.get(i));
                projectMap.put("list",projectList);
            }else if(22==permissionsList.get(i).getParentPermId()){
                //goodsList.add(permissionsList.get(i));
                goodsMap.put("list",goodsList);
            }else if(31==permissionsList.get(i).getParentPermId()){
                //pricesList.add(permissionsList.get(i));
                pricesMap.put("list",pricesList);
            }else if(37==permissionsList.get(i).getParentPermId()){
                //supplierList.add(permissionsList.get(i));
                supplierMap.put("list",supplierList);
            }else if(47==permissionsList.get(i).getParentPermId()){
                //agentList.add(permissionsList.get(i));
                agentMap.put("list",agentList);
            }else if(53==permissionsList.get(i).getParentPermId()){
                //agentList.add(permissionsList.get(i));
                purchaseResultsMap.put("list",purchaseResultsList);
            }else if(57==permissionsList.get(i).getParentPermId()){
                //bidsList.add(permissionsList.get(i));
                bidsMap.put("list",bidsList);
            }else if(61==permissionsList.get(i).getParentPermId()){
                //contractList.add(permissionsList.get(i));
                contractMap.put("list",contractList);
            }else if(65==permissionsList.get(i).getParentPermId()){
                //templateList.add(permissionsList.get(i));
                templateMap.put("list",templateList);
            }else if(81==permissionsList.get(i).getParentPermId()){
                //systemList.add(permissionsList.get(i));
                systemMap.put("list",systemList);
            }


            if(2==permissionsList.get(i).getParentPermId()){
                firstPageListtodo.add(permissionsList.get(i));
                firstPageMaptodo.put("list",firstPageListtodo);
            }
            if(3==permissionsList.get(i).getParentPermId()){
                firstPageListdone.add(permissionsList.get(i));
                firstPageMapdone.put("list",firstPageListdone);
            }
            if(4==permissionsList.get(i).getParentPermId()){
                firstPageListpending.add(permissionsList.get(i));
                firstPageMappending.put("list",firstPageListpending);
            }
            if(5==permissionsList.get(i).getParentPermId()){
                firstPageListread.add(permissionsList.get(i));
                firstPageMapread.put("list",firstPageListread);
            }
            if(15==permissionsList.get(i).getParentPermId()){
                projectListMyProject.add(permissionsList.get(i));
                projectMapMyProject.put("list",projectListMyProject);
            }
            if(23==permissionsList.get(i).getParentPermId()){
                goodsListMyGoods.add(permissionsList.get(i));
                goodsMapMyGoods.put("list",goodsListMyGoods);
            }
            if(32==permissionsList.get(i).getParentPermId()){
                pricesListMyPrice.add(permissionsList.get(i));
                pricesMapMyPrice.put("list",pricesListMyPrice);
            }
            if(38==permissionsList.get(i).getParentPermId()){
                supplierListMySupplier.add(permissionsList.get(i));
                supplierMapMySupplier.put("list",supplierListMySupplier);
            }
            if(39==permissionsList.get(i).getParentPermId()){
                supplierListInfo.add(permissionsList.get(i));
                supplierMapInfo.put("list",supplierListInfo);
            }
            if(40==permissionsList.get(i).getParentPermId()){
                supplierListUpdate.add(permissionsList.get(i));
                supplierMapUpdate.put("list",supplierListUpdate);
            }
            if(41==permissionsList.get(i).getParentPermId()){
                supplierListResetPWD.add(permissionsList.get(i));
                supplierMapResetPWD.put("list",supplierListResetPWD);
            }
            if(48==permissionsList.get(i).getParentPermId()){
                agentListMyAgent.add(permissionsList.get(i));
                agentMapMyAgent.put("list",agentListMyAgent);
            }
            if(92==permissionsList.get(i).getParentPermId()){
                agentListInfo.add(permissionsList.get(i));
                agentMapMyAgent.put("list",agentListInfo);
            }
            if(93==permissionsList.get(i).getParentPermId()){
                agentListUpdate.add(permissionsList.get(i));
                agentMapUpdate.put("list",agentListUpdate);
            }
            if(94==permissionsList.get(i).getParentPermId()){
                agentListResetPWD.add(permissionsList.get(i));
                agentMapResetPWD.put("list",agentListResetPWD);
            }
            if(54==permissionsList.get(i).getParentPermId()){
                purchaseResultsListMy.add(permissionsList.get(i));
                purchaseResultsMapMy.put("list",purchaseResultsListMy);
            }
            if(58==permissionsList.get(i).getParentPermId()){
                bidsListMyBids.add(permissionsList.get(i));
                bidsMapMyBids.put("list",bidsListMyBids);
            }
            if(62==permissionsList.get(i).getParentPermId()){
                contractListMy.add(permissionsList.get(i));
                contractMapMy.put("list",contractListMy);
            }
            if(66==permissionsList.get(i).getParentPermId()){
                templateListPurchase.add(permissionsList.get(i));
                templateMapPurchase.put("list",templateListPurchase);
            }
            if(67==permissionsList.get(i).getParentPermId()){
                templateListBids.add(permissionsList.get(i));
                templateMapBids.put("list",templateListBids);
            }
            if(68==permissionsList.get(i).getParentPermId()){
                templateListContract.add(permissionsList.get(i));
                templateMapContract.put("list",templateListContract);
            }
            if(82==permissionsList.get(i).getParentPermId()){
                systemListUser.add(permissionsList.get(i));
                systemMapUser.put("list",systemListUser);
            }
            if(83==permissionsList.get(i).getParentPermId()){
                systemListRole.add(permissionsList.get(i));
                systemMapRole.put("list",systemListRole);
            }


        }

        if(systemMapRole.size()>0){
            systemList.add(systemMapRole);
        }
        if(systemMapUser.size()>0){
            systemList.add(systemMapUser);
        }
        if(templateMapContract.size()>0){
            templateList.add(templateMapContract);
        }
        if(templateMapBids.size()>0){
            templateList.add(templateMapBids);
        }
        if(templateMapPurchase.size()>0){
            templateList.add(templateMapPurchase);
        }
        if(contractMapMy.size()>0){
            contractList.add(contractMapMy);
        }
        if(bidsMapMyBids.size()>0){
            bidsList.add(bidsMapMyBids);
        }
        if(purchaseResultsMapMy.size()>0){
            purchaseResultsList.add(purchaseResultsMapMy);
        }
        if(agentMapResetPWD.size()>0){
            agentList.add(agentMapResetPWD);
        }
        if(agentMapUpdate.size()>0){
            agentList.add(agentMapUpdate);
        }
        if(agentMapInfo.size()>0){
            agentList.add(agentMapInfo);
        }
        if(agentMapMyAgent.size()>0){
            agentList.add(agentMapMyAgent);
        }
        if(supplierMapResetPWD.size()>0){
            supplierList.add(supplierMapResetPWD);
        }
        if(supplierMapUpdate.size()>0){
            supplierList.add(supplierMapUpdate);
        }
        if(supplierMapInfo.size()>0){
            supplierList.add(supplierMapInfo);
        }
        if(supplierMapMySupplier.size()>0){
            supplierList.add(supplierMapMySupplier);
        }
        if(pricesMapMyPrice.size()>0){
            pricesList.add(pricesMapMyPrice);
        }
        if(goodsMapMyGoods.size()>0){
            goodsList.add(goodsMapMyGoods);
        }
        if(projectMapMyProject.size()>0){
            projectList.add(projectMapMyProject);
        }
        if(firstPageMapread.size()>0){
            firstPageList.add(firstPageMapread);
        }
        if(firstPageMappending.size()>0){
            firstPageList.add(firstPageMappending);
        }
        if(firstPageMapdone.size()>0){
            firstPageList.add(firstPageMapdone);
        }
        if(firstPageMaptodo.size()>0){
            firstPageList.add(firstPageMaptodo);
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
        if(purchaseResultsMap.size()>0){
           returnMap.add(purchaseResultsMap);
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
