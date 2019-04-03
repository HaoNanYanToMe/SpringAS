package generate;


import com.prism.springas.api.BaseApi;
import com.prism.springas.api.impl.BaseApiImpl;
import com.prism.springas.utils.BasePage;
import com.prism.springas.utils.pdmHelper.generate.PdmToJavaGenerate;
import com.prism.springas.utils.sqlEngine.sqlEngine;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/*
    -根据pdm生成相关代码文件
 */
public class generateTest {

    public static void main(String[] args) throws Exception {

        //Barthelndex.pdm
        //api.pdm
        String url = "testSpringAS.pdm";
        PdmToJavaGenerate pdmToJavaGenerate = new PdmToJavaGenerate();
        pdmToJavaGenerate.baseDaoGenerate(url);
        pdmToJavaGenerate.baseCfgGenerate(url);
        pdmToJavaGenerate.baseSQLGenerate(url);
//        sqlEngine sqlEngine = new sqlEngine();

    }
}
