package com.etone.protocolsupply.utils;

import com.etone.protocolsupply.model.entity.project.ProjectInfo;
import com.etone.protocolsupply.model.entity.user.User;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ImageUtil {

    private static void createImage(String path, String dstName, BufferedImage dstImage) {
        try {
            File uploadPath = new File(path);
            if (!uploadPath.exists()) {
                uploadPath.mkdirs();
            }

            String formatName = dstName.substring(dstName.lastIndexOf(".") + 1);
            ImageIO.write(dstImage,formatName,new File(dstName));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static void getImage(ProjectInfo projectInfo, User creator, String imageType,String path,String FileName,String agentName){

        Map<String, String> map1 = new HashMap<String, String>();
        map1.put("项目编号", projectInfo.getProjectCode());
        map1.put("项目名称", projectInfo.getProjectSubject());
        if("采购结果通知书".equals(imageType)){
            map1.put("成交供应商", projectInfo.getCreator());
            map1.put("成交金额", "人民币"+projectInfo.getAmountRmb());
        }else{
            map1.put("用户单位",creator.getCompany());
            map1.put("中标金额","人民币"+projectInfo.getAmountRmb());

        }


        int imageWidth = 2100;// 图片的宽度  A4纸尺寸
        int imageHeight = 2970;// 图片的高度

        BufferedImage image = new BufferedImage(imageWidth, imageHeight,
                BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.getGraphics();
        graphics.setColor(Color.white);
        graphics.fillRect(0, 0, imageWidth, imageHeight);
        graphics.setColor(Color.black);

        //标题
        int high = 280;
        int wigth = 0;
        graphics.setFont(new Font("仿宋", Font.BOLD, 130));
        graphics.drawString("华南理工大学招标中心", 372, high);

        //标题下横线
        high += 72;
        Graphics2D graphics2D=(Graphics2D)graphics;
        graphics2D.setStroke(new BasicStroke(3.0f));
        graphics2D.drawLine(200, high, 1900, high);//1356

        //副标题
        high += 230;
        graphics.setFont(new Font("宋体", Font.BOLD, 82));
        graphics.drawString(imageType, 785, high);         //TODO

        //通知接收人
        high += 320;
        graphics.setFont(new Font("宋体", Font.PLAIN, 60));
        if("采购结果通知书".equals(imageType)){
            graphics.drawString(creator.getCompany()+"：", 250, high);   //TODO
        }else {
            graphics.drawString(agentName+"：", 250, high);
        }

        //通知
        high += 150;
        graphics.setFont(new Font("宋体", Font.PLAIN, 60));
        if("采购结果通知书".equals(imageType)){
            graphics.drawString("经项目评审委员会决定，采购结果如下：", 440, high);   //TODO
        }else {
            graphics.drawString("评审工作已经圆满结束，现确定贵单位为本项目", 440, high);
            high += 150;
            graphics.setFont(new Font("宋体", Font.PLAIN, 60));
            graphics.drawString("的中标供应商，中标内容如下：", 250, high);
        }


        for(Map.Entry<String, String> entry : map1.entrySet()){
            String name = entry.getKey() + "：   " + entry.getValue();
            high += 150;
            wigth = 440;
            graphics.setFont(new Font("宋体", Font.PLAIN, 60));
            graphics.drawString(name, wigth, high);
        }

        //落款
        high += 500;
        graphics.setFont(new Font("宋体", Font.PLAIN, 60));
        graphics.drawString("华南理工大学招标中心", 1200, high);

        //落款时间
        high += 150;
        graphics.setFont(new Font("宋体", Font.PLAIN, 60));
        SimpleDateFormat format = new SimpleDateFormat("YYYY年MM月dd日");
        graphics.drawString(format.format(new Date()), 1300, high);

        //底部横线
        high += 250;
        graphics2D.setStroke(new BasicStroke(3.0f));
        graphics2D.drawLine(200, high, 1900, high);//1356

        //备注信息
        high += 70;
        graphics.setFont(new Font("宋体", Font.PLAIN, 40));
        graphics.drawString("备注：", 200, high);
        high += 70;
        graphics.setFont(new Font("宋体", Font.PLAIN, 40));
        graphics.drawString("1)本通知书为采购单位报账凭证;", 200, high);
        high += 70;
        graphics.setFont(new Font("宋体", Font.PLAIN, 40));
        graphics.drawString("2)请尽快与成交供应商联系，商定合同事宜。", 200, high);


        createImage(path,FileName, image);

    }
}
