package com.etone.protocolsupply.utils;

import com.etone.protocolsupply.model.entity.project.ProjectInfo;
import com.etone.protocolsupply.model.entity.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ImageUtil {

    private static final Logger logger = LoggerFactory.getLogger(ImageUtil.class);

    private static void createImage(String path, String dstName, BufferedImage dstImage) {
        try {
            File uploadPath = new File(path);
            if (!uploadPath.exists()) {
                uploadPath.mkdirs();
            }

            String formatName = dstName.substring(dstName.lastIndexOf(".") + 1);
            ImageIO.write(dstImage,formatName,new File(dstName));

        } catch (Exception e) {
            logger.error("生成图片异常",e.getMessage());
        }
    }



    public static void getImage(ProjectInfo projectInfo, String agentCompanyName, String imageType, String path, String FileName, String finalUser){

        Map<String, String> map1 = new HashMap<String, String>();
        map1.put("项目编号", projectInfo.getProjectCode());
        map1.put("项目名称", projectInfo.getProjectSubject());
        if("采购结果通知书".equals(imageType)){
            map1.put("成交供应商", agentCompanyName);
            map1.put("成交金额", projectInfo.getCurrency()+projectInfo.getAmount());
        }else{
            map1.put("用户单位",finalUser);
            map1.put("成交金额",projectInfo.getCurrency()+projectInfo.getAmount());

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
            graphics.drawString(finalUser+"：", 250, high);   //TODO
        }else {
            graphics.drawString(agentCompanyName+"：", 250, high);
        }

        //通知
        high += 150;
        graphics.setFont(new Font("宋体", Font.PLAIN, 60));
        if("采购结果通知书".equals(imageType)){
            graphics.drawString("贵单位通过科研设备协议供货平台申请采购的", 420, high);   //TODO
            high += 150;
            graphics.setFont(new Font("宋体", Font.PLAIN, 60));
            graphics.drawString("项目已通过审核，采购结果如下：", 250, high);
        }else {
            graphics.drawString("经审核，现确定贵单位为本项目成交供应商，", 420, high);
            high += 150;
            graphics.setFont(new Font("宋体", Font.PLAIN, 60));
            graphics.drawString("成交内容如下：", 250, high);
        }


        for(Map.Entry<String, String> entry : map1.entrySet()){
            String name = entry.getKey() + "：" + entry.getValue();
            high += 150;
            wigth = 420;
            graphics.setFont(new Font("宋体", Font.PLAIN, 60));
            graphics.drawString(name, wigth, high);
        }

        //落款
        high += 320;
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
        graphics.drawString("依照有关法规规定，应自本通知书发出之日起30日内签订书面合同。", 200, high);
        //high += 70;
        //graphics.setFont(new Font("宋体", Font.PLAIN, 40));
        //graphics.drawString("2)请尽快与成交供应商联系，商定合同事宜。", 200, high);

        createImage(path,FileName, image);

    }

    /**
     * 给图片添加水印、可设置水印图片旋转角度
     * @param iconPath 水印图片路径
     * @param srcImgPath 源图片路径
     * @param targerPath 目标图片路径
     * @param degree 水印图片旋转角度
     * @param imageType
     */
    public static void markImageByIcon(String iconPath, String srcImgPath,
                                       String targerPath, Integer degree, String imageType) {
        OutputStream os = null;
        try {
            Image srcImg = ImageIO.read(new File(srcImgPath));

            BufferedImage buffImg = new BufferedImage(srcImg.getWidth(null),
                    srcImg.getHeight(null), BufferedImage.TYPE_INT_RGB);

            // 得到画笔对象
            // Graphics g= buffImg.getGraphics();
            Graphics2D g = buffImg.createGraphics();

            // 设置对线段的锯齿状边缘处理
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            g.drawImage(srcImg.getScaledInstance(srcImg.getWidth(null), srcImg
                    .getHeight(null), Image.SCALE_SMOOTH), 0, 0, null);

            if (null != degree) {
                // 设置水印旋转
                g.rotate(Math.toRadians(degree),
                        (double) buffImg.getWidth() / 2, (double) buffImg
                                .getHeight() / 2);
            }

            // 水印图象的路径 水印一般为gif或者png的，这样可设置透明度
            ImageIcon imgIcon = new ImageIcon(iconPath);

            // 得到Image对象。
            Image img = imgIcon.getImage();

            float alpha = 1f; // 透明度
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,
                    alpha));

            // 表示水印图片的位置
            //int hight =(imageType=="成交通知书"?2065:1920);
            int hight =2065;

            g.drawImage(img, 1290, hight, null);

            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));

            g.dispose();

            os = new FileOutputStream(targerPath);

            // 生成图片
            ImageIO.write(buffImg, "JPG", os);

            System.out.println("图片完成添加Icon印章");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
