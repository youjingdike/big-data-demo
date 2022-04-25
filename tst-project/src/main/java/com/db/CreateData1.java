package com.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateData1 {
    
    public void createData() {
      //插入的最大查询码01PICC450013001362473191085688
        
        String policyQueryNo = "";
        String policyConfirmNo = "";
        //vin码
        String frameNo = "";
        String frameLastSixNo = "";
        //发动机号
        String engineNo = "";
        String engineLastSixNo = "";
        //号牌号码
        String licenseNo = "";

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String dateStr = "";
        String sql = "insert into IATCITEMCAR (POLICYQUERYNO, AREACODE, POLICYCONFIRMNO, POLICYNO, COMPANYCODE, LICENSERCODE, LICENSENO, LICENSECOLORCODE, LICENSETYPE, CARKINDCODE, USENATURECODE, ENGINENO, FRAMENO, USEYEARS, RUNMILES, NEWCARFLAG, ECDEMICCARFLAG, MANUFACTORY, BRANDNAME, MODELCODE, AGREEDDRIVERCOUNT, BIZSTATUS, REMARK, SPECIALCARFLAG, ENGINELASTSIXNO, FRAMELASTSIXNO, REGISTERDATE, NODAMAGEYEARS, VEHICLESTYLE, LIMITLOADPERSON, LIMITLOAD, WHOLEWEIGHT, DISPLACEMENT, POWER, OWNERNAME, OWNERTYPE, FUELTYPE) values (?, '450000', ?, '', 'PICC', '', ?, '', '02', '11', '101', ?, ?, 1, 30000, '0', '0', '', 'haha', 'JD1-1086', 2, '2', '', ' ', ?, ?, ?, 0, 'K11', 10, 10, 10, 10, 10.00, '', '', '0')";
        Connection  conn = DBUtil1.getConn();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
        } catch (SQLException e) {
            DBUtil1.close(ps, conn);
            e.printStackTrace();
        }
        
        StringBuilder sb_Query = new StringBuilder();
        StringBuilder sb_Conf = new StringBuilder();
        StringBuilder sb_FrameNo = new StringBuilder();
        StringBuilder sb_EngineNo = new StringBuilder();
        StringBuilder sb_LicenseNo = new StringBuilder();
        
        for (int i=2000000; i < 4700000; i++){
            
            sb_Query.append("01PICC45001300136247310");
            sb_Conf.append("02PICC45001300136247320");
            sb_FrameNo.append("LVSHBFAB18");
            sb_EngineNo.append("112PPPB");
            sb_LicenseNo.append("晋DB6");
            
            if (i < 10) {
                sb_Query.append("000000" + i);
                sb_Conf.append("000000" + i);
                sb_FrameNo.append("000000" + i);
                sb_EngineNo.append("000000" + i);
                sb_LicenseNo.append("000000" + i);
            } else if (i < 100) {
                sb_Query.append("00000" + i);
                sb_Conf.append("00000" + i);
                sb_FrameNo.append("00000" + i);
                sb_EngineNo.append("00000" + i);
                sb_LicenseNo.append("00000" + i);
            } else if (i < 1000) {
                sb_Query.append("0000" + i);
                sb_Conf.append("0000" + i);
                sb_FrameNo.append("0000" + i);
                sb_EngineNo.append("0000" + i);
                sb_LicenseNo.append("0000" + i);
            } else if (i < 10000) {
                sb_Query.append("000" + i);
                sb_Conf.append("000" + i);
                sb_FrameNo.append("000" + i);
                sb_EngineNo.append("000" + i);
                sb_LicenseNo.append("000" + i);
            } else if (i < 100000) {
                sb_Query.append("00" + i);
                sb_Conf.append("00" + i);
                sb_FrameNo.append("00" + i);
                sb_EngineNo.append("00" + i);
                sb_LicenseNo.append("00" + i);
            } else if (i < 1000000) {
                sb_Query.append("0" + i);
                sb_Conf.append("0" + i);
                sb_FrameNo.append("0" + i);
                sb_EngineNo.append("0" + i);
                sb_LicenseNo.append("0" + i);
            } else {
                sb_Query.append(i);
                sb_Conf.append(i);
                sb_FrameNo.append(i);
                sb_EngineNo.append(i);
                sb_LicenseNo.append(i);
            }
            
            policyQueryNo = sb_Query.toString();
            policyConfirmNo = sb_Conf.toString();
            frameNo = sb_FrameNo.toString();
            engineNo = sb_EngineNo.toString();
            licenseNo = sb_LicenseNo.toString();
            
            sb_Query.setLength(0);
            sb_Conf.setLength(0);
            sb_FrameNo.setLength(0);
            sb_EngineNo.setLength(0);
            sb_LicenseNo.setLength(0);
            
            frameLastSixNo = frameNo.substring(frameNo.length()-6, frameNo.length());
            engineLastSixNo = engineNo.substring(engineNo.length()-6, engineNo.length());
            
            dateStr = format.format(new Date());
            
            try {
                ps.setString(1,policyQueryNo);
                ps.setString(2,policyConfirmNo);
                ps.setString(3,licenseNo);
                ps.setString(4,engineNo);
                ps.setString(5,frameNo);
                ps.setString(6,engineLastSixNo);
                ps.setString(7,frameLastSixNo);
                ps.setString(8,dateStr);
                ps.executeUpdate();
                System.out.println("i=" + i);
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("i=" + i);
                continue;
            }
        }
        
        DBUtil1.close(ps, conn);
    }
    
    public static void main(String[] args) {
        CreateData1 createDate1 = new CreateData1();
        createDate1.createData();
    }
    
}
