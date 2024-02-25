import com.czc.Entity.DTO.User2FileDTO;
import com.czc.Service.FileService;
import com.czc.Service.Impl.FileServiceImpl;
import com.czc.Service.Impl.ShareServiceImpl;
import com.czc.Service.Impl.VoServiceImpl;
import com.czc.Service.ShareService;
import com.czc.Service.VoService;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@SpringBootTest
public class test {

    private ShareService shareService = new ShareServiceImpl();

    @Test
    public void generate(){
        System.out.println(shareService.generateExtractCode());
    }

    @Test
    public void test3(){
        StringBuilder sql = new StringBuilder();
        sql.append("  SELECT * FROM (");
        sql.append("  SELECT w.id,rownum rn, w.planno plan_no, w.appStateid app_state,");
        sql.append("  TO_CHAR((SYSDATE - s.SENDDATE)*24*60,'FM9999990') OVERTIME, ");
        sql.append("  (SELECT name FROM PMCS.SYS_DM_CODEINFO WHERE fname = 'JXJH_JHLX' AND w.plantype = code) plan_type ,");
        sql.append("  TO_CHAR(w.PLANENDTIME, 'yyyy-MM-dd hh24:mi:ss') plan_end_time,");
        sql.append("  w.unitName unit_name, w.county, TO_CHAR(w.planstarttime, 'yyyy-MM-dd hh24:mi:ss') plan_start_time, w.content, ");
        sql.append("  (SELECT WM_CONCAT(tg_name) from  PSSC.T_BIZC_OUTAGE_PLAN_EQUIP p where p.jxjhid = w.planid ) tg_name, ");
        sql.append("  ( SELECT p.bdzmc from  PSSC.T_BIZC_OUTAGE_PLAN_EQUIP p where p.jxjhid = w.planid and  rownum = 1  ) bdzmc,");
        sql.append("  (SELECT p.xlmc from  PSSC.T_BIZC_OUTAGE_PLAN_EQUIP p where p.jxjhid = w.planid  and rownum = 1) xlmc,");
        sql.append("  w.impactSHS impact_SHS,w.countytheannualshs,w.countytheannualshsyears,w.note,");
        sql.append("  TO_CHAR(w.ARRIVAL_TIME, 'yyyy-MM-dd hh24:mi:ss') ARRIVAL_TIME, ");
        sql.append("   TO_CHAR(w.EXPLORATIONSTARTTIME, 'yyyy-MM-dd hh24:mi:ss') EXPLORATION_START_TIME,");
        sql.append("   TO_CHAR(w.EXPLORATIONENDTIME, 'yyyy-MM-dd hh24:mi:ss') exploration_end_time,");
        sql.append("  w.EXPLORATIONPEOPLE exploration_people,w.POWERRANGE power_range,");
        sql.append("  (SELECT  name FROM PMCS.SYS_DM_CODEINFO T  WHERE T.FNAME ='JXJH_RISK_LEVEL' and code = w.rsklvel) risk_level , ");
        sql.append("  w.EXPLORATIONADDRESS exploration_address, w.EXPLORATIONENVIRONMENT exploration_environment, w.EXPLORATIONDESCRIBE exploration_describe,");
        sql.append("  w.SYSTEMMOVE system_move, w.TEAMBEARING team_bearing, w.NOBEARING nobearing, r.PARTICULAR_CHANGES_INFO");
        sql.append("  FROM (SELECT PLANNO,SENDDATE FROM pssc.exploration_sendorders s WHERE ORDERRENID = ? AND SENDTYPE = '1') s");
//        paramList.add(userId);
        sql.append("  LEFT JOIN PSSC.Exploration_workOrder w ON w.PLANNO = s.PLANNO");

        sql.append("  LEFT JOIN PSSC.T_BIZC_OUTAGE_PLAN_REPORT r ON w.PLANID = r.id");
        sql.append("  WHERE 1 = 1 ");
        System.out.println(sql);
    }

    @Test
    public void test2(){
        StringBuilder sql = new StringBuilder();
        sql.append("select  w.id, planno, ");
        sql.append("  (select name from PMCS.SYS_DM_CODEINFO c  where c.fname = 'JXJH_JHLX' and code = plantype) plantype, ");
        //sql.append(" (select name from  PMCS.SYS_DM_CODEINFO where fname = 'JXJH_CORE_TYPE' and plantype = code) plantype, ");
        sql.append(" (select VALUE from  PSSC.t_interface_code tmp where type='QHJX_JHZT' AND CODE = stateType)  stateType, ");
        sql.append(" (select VALUE from  PSSC.t_interface_code tmp where type='QHJX_JHZT' AND CODE = appStateid) " );
        //sql.append(" appStateid,unitName,county,  TO_char(planstarttime, 'yyyy-MM-dd hh24:mi:ss') ||  ' 至 ' ||  TO_char(planendtime, 'yyyy-MM-dd hh24:mi:ss') planstarttime,content,PowerRange ,impactSHS , ");
        sql.append(" appStateid,unitName,county,  TO_char(planstarttime, 'yyyy-MM-dd hh24:mi:ss') planstarttime , TO_char(planendtime, 'yyyy-MM-dd hh24:mi:ss') planendtime,content,PowerRange ,impactSHS , ");
        sql.append(" ( SELECT p.bdzmc from  PSSC.T_BIZC_OUTAGE_PLAN_EQUIP p where p.jxjhid = w.planid and  rownum = 1  ) bdzmc, ");
        sql.append(" (SELECT p.xlmc from  PSSC.T_BIZC_OUTAGE_PLAN_EQUIP p where p.jxjhid = w.planid  and rownum = 1) xlmc ,");
        sql.append(" (SELECT WM_CONCAT(tg_name) from  PSSC.T_BIZC_OUTAGE_PLAN_EQUIP p where p.jxjhid = w.planid ) tg_name , ");
        sql.append("  TO_char(explorationstarttime, 'yyyy-MM-dd hh24:mi:ss') explorationstarttime,  TO_char(explorationendtime, 'yyyy-MM-dd hh24:mi:ss')  explorationendtime ");
        sql.append("   ,  (SELECT  name FROM PMCS.SYS_DM_CODEINFO T  WHERE T.FNAME ='JXJH_RISK_LEVEL' and code = rsklvel) rsklvel , ");
        sql.append("   decode(w.systemmove,'1','是','0','否') systemmove  ,  decode(w.teambearing,'1','是','0','否')  teambearing ,  explorationdescribe ");
        sql.append(" from PSSC.Exploration_workOrder w  where 1=1 ");
        System.out.println(sql);
    }

    @Test
    public void test(){
        String encode1 = "MTIzNDU2Nw==";
        String str = "1234567";
        byte[] encode = Base64.getEncoder().encode(str.getBytes(StandardCharsets.UTF_8));
        System.out.println(new String(encode));
        String decode = new String(Base64.getDecoder().decode(encode1));
        System.out.println(decode);

    }

    @Test
    public void fileServiceTest(){
        FileService fileService = new FileServiceImpl();
        VoService voService = new VoServiceImpl();
        User2FileDTO u2f = voService.selectUser2FileDTOById("1011b615828b4c118fe098bf56bff3bc");
        System.out.println(fileService.getById(u2f.getFileId()));
    }
}
