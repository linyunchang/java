package ${packageName};

import org.springframework.stereotype.Service;

import pers.lyc.mybatis.service.MysqlService;
import ${tableCache.className};

/**
 * @author ${author}
 * @since ${nowDate}
 * @comment ${tableCache.cnName}的Service
 */
@Service
public class ${tableCache.classSimpleName}Service extends MysqlService<${tableCache.classSimpleName}> {
	
//	@Autowired
//	private ${tableCache.classSimpleName}Mapper ${tableCache.classSimpleName?uncap_first}Mapper;
	
}
