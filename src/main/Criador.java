package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Criador {
	
	static String packageNamePrincipal = "br.com.memora.ematerconsultaserver.api";
	static String caminhoProjetoRaiz = "C:\\areatrabalho\\projetos\\Eclipse\\emater\\emater-consulta-server\\src\\main\\java\\br\\com\\memora\\ematerconsultaserver\\api";
	
	static Set<String> imports = new TreeSet<>();
	
	public static void main(String[] args) {
		try {
			FileReader lerParanaue = new FileReader(Criador.class.getResource("").getPath() + "arquivo.txt");
            //Caixa dagua de leitura
            BufferedReader caixaDaguaDeLeitura = new BufferedReader(lerParanaue);

            String linha = "";
            List<List<String>> tabelas = new LinkedList<>();
            List<String> linhas = null;
            while ((linha = caixaDaguaDeLeitura.readLine()) != null) {
            	if(linha.contains("Table:")) {
            		if(linhas != null) {
            			tabelas.add(new LinkedList<>(linhas));
            		}
            		linhas = new LinkedList<>();
            	}
        		linhas.add(linha);
            }
            if(linhas != null) {
    			tabelas.add(new LinkedList<>(linhas));
    		}
            caixaDaguaDeLeitura.close();
            
            for(List<String> tabela : tabelas) {
        		if(tabela != null && tabela.size() > 0) {
        			criar(tabela);
        		}
            }
		} catch (Exception e) {
            e.printStackTrace();
		}
		System.out.println("Finalizado com sucesso!");
	}
	
	private static void criar(List<String> tabela) {
		String nomeDaTabela = "";
		String nomeDaClasse = "";
		try {
            List<Linha> linhas = new LinkedList<>();
            Linha pk = null;
            for (String linha : tabela) {
            	if(linha.contains("Table:")) {
            		nomeDaTabela = linha.replaceAll("Table: ", "").trim();
            		nomeDaClasse = getClassName(nomeDaTabela);
            	}
            	else if(!linha.contains("Columns:")) {
            		final String[] vet = linha.split(" ");
            		final String tipo = getTipo(vet[1]);
            		final String colum = vet[0];
            		final String nome = getFieldName(colum);
            		Linha aux = new Linha(tipo, colum, nome, linha.contains("PK"));
            		if(aux.getPk()) {
            			pk = aux;
            		}
            		else {
            			linhas.add(aux);
            		}
            	}
            }
            linhas.sort((a, b) -> a.getTipo().compareTo(b.getTipo()));
            getModel(nomeDaTabela, nomeDaClasse, linhas, pk);
            getRepository(nomeDaClasse, pk);
            getService(nomeDaClasse, pk);
            getServiceImp(nomeDaClasse, pk);
            getController(nomeDaClasse, pk);
		} catch (Exception e) {
            e.printStackTrace();
		}
	}
	
	private static void getController(String nomeDaClasse, Linha pk) {
		String classe = "package " + packageNamePrincipal + "." + "controller" + ";\n" +  
				"\r\n" + 
				"import org.springframework.beans.factory.annotation.Autowired;\r\n" + 
				"import org.springframework.web.bind.annotation.RequestMapping;\r\n" + 
				"import org.springframework.web.bind.annotation.RestController;\r\n" + 
				"\r\n" + 
				"import " + packageNamePrincipal + ".controller.abstracts.ManterControllerBeanBasic;\r\n" + 
				"import " + packageNamePrincipal + ".model." + nomeDaClasse + ";\r\n" + 
				"import " + packageNamePrincipal + ".service." + nomeDaClasse + "Service;\r\n" + 
				"import lombok.Getter;\r\n" + 
				"\r\n" + 
				"@RestController\r\n" + 
				"@RequestMapping(\"/api/" + nomeDaClasse.toLowerCase() + "\")\r\n" + 
				"public class " + nomeDaClasse + "Controller extends ManterControllerBeanBasic<" + nomeDaClasse + ">{\r\n" + 
				"\r\n" + 
				"	@Getter\r\n" + 
				"	@Autowired\r\n" + 
				"	private " + nomeDaClasse + "Service service;\r\n" + 
				"}\r\n";
		incluir(classe, caminhoProjetoRaiz + "\\controller\\" + nomeDaClasse + "Controller.java");
	}
	
	private static void getRepository(String nomeDaClasse, Linha pk) {
		String classe = "package " + packageNamePrincipal + "." + "repository" + ";\n" + 
				"\r\n" + 
				"import org.springframework.data.jpa.repository.JpaRepository;\r\n" + 
				"\r\n" + 
				"import " + packageNamePrincipal + ".model." + nomeDaClasse + ";\r\n" + 
				"\r\n" + 
				"public interface " + nomeDaClasse + "Repository extends JpaRepository<" + nomeDaClasse + ", " + pk.getTipo() + ">{}";
		incluir(classe, caminhoProjetoRaiz + "\\repository\\" + nomeDaClasse + "Repository.java");
	}
	
	private static void getService(String nomeDaClasse, Linha pk) {
		String classe = "package " + packageNamePrincipal + "." + "service" + ";\n" + 
				"\r\n" + 
				"import org.springframework.stereotype.Component;\r\n" + 
				"\r\n" + 
				"import " + packageNamePrincipal + ".model." + nomeDaClasse + ";\r\n" +  
				"import " + packageNamePrincipal + ".util.CRUDPadraoService;\r\n" + 
				"\r\n" + 
				"@Component\r\n" + 
				"public interface " + nomeDaClasse + "Service extends CRUDPadraoService<" + nomeDaClasse + ">{}\r\n";
		incluir(classe, caminhoProjetoRaiz + "\\service\\" + nomeDaClasse + "Service.java");
	}
	
	private static void getServiceImp(String nomeDaClasse, Linha pk) {
		String classe = "package " + packageNamePrincipal + "." + "service.imp" + ";\n" + 
				"\r\n" + 
				"import org.apache.commons.logging.Log;\r\n" + 
				"import org.apache.commons.logging.LogFactory;\r\n" + 
				"import org.springframework.beans.factory.annotation.Autowired;\r\n" + 
				"import org.springframework.stereotype.Service;\r\n" + 
				"\r\n" + 
				"import " + packageNamePrincipal + ".repository.hql.GenericDAO;\r\n" + 
				"import " + packageNamePrincipal + ".service." + nomeDaClasse + "Service;\r\n" + 
				"import " + packageNamePrincipal + ".model." + nomeDaClasse + ";\r\n" + 
				"import " + packageNamePrincipal + ".repository." + nomeDaClasse + "Repository;\r\n" + 
				"import lombok.Getter;\r\n" + 
				"\r\n" + 
				"@Getter\r\n" + 
				"@Service\r\n" + 
				"public class " + nomeDaClasse + "ServiceImp implements " + nomeDaClasse + "Service{\r\n" + 
				"\r\n" + 
				"	private static final Log logger = LogFactory.getLog(" + nomeDaClasse + "Service.class);\r\n" + 
				"	\r\n" + 
				"	@Autowired\r\n" + 
				"	private " + nomeDaClasse + "Repository persistencia;\r\n" + 
				"	\r\n" + 
				"	@Autowired\r\n" + 
				"	private GenericDAO genericDAO;\r\n" + 
				"	\r\n" + 
				"	@Override\r\n" + 
				"	public " + nomeDaClasse + " findByField(String field, Object value) {\r\n" + 
				"		try {\r\n" + 
				"			" + nomeDaClasse + " objeto = genericDAO.findByField(" + nomeDaClasse + ".class, field, value);\r\n" + 
				"			return objeto;\r\n" + 
				"		} catch (Exception e) {\r\n" + 
				"			logger.warn(\"findByField \" + e.getMessage());\r\n" + 
				"			return null;\r\n" + 
				"		}\r\n" + 
				"	}\r\n" + 
				"	\r\n" + 
				"	@Override\r\n" + 
				"	public Log getLogger() {\r\n" + 
				"		return logger;\r\n" + 
				"	}\r\n" + 
				"}\r\n";
		incluir(classe, caminhoProjetoRaiz + "\\service\\imp\\" + nomeDaClasse + "ServiceImp.java");
	}
	
	private static void getModel(String nomeDaTabela, String nomeDaClasse, List<Linha> linhas, 
			Linha pk) {
		String classe = "package " + packageNamePrincipal + "." + "model" + ";\n" +
				"\n" +
				"import java.io.Serializable;\n" +
				"\n" +
				"import javax.persistence.Column;\n" +
				"import javax.persistence.Entity;\n" +
				"import javax.persistence.GeneratedValue;\n" +
				"import javax.persistence.GenerationType;\n" +
				"import javax.persistence.Id;\n" +
				"import javax.persistence.Table;\n" +
				"\n" +
				"import lombok.AllArgsConstructor;\n" +
				"import lombok.Data;\n" +
				"import lombok.NoArgsConstructor;";
		if(imports.size() > 0) {
			for(String im : imports) {
				classe += im;
			}
			imports = new TreeSet<>();
		}
		classe += "\n\n" +
				"/**\n" +
				" * \n" +
				" * @author Suleiman Moraes\n" +
				" *\n" +
				" */\n" +
				"@Data\n" +
				"@AllArgsConstructor\n" +
				"@NoArgsConstructor\n" +
				"@Entity\n" +
				"@Table(name = \"" + nomeDaTabela + "\")\n" +
				"public class " + nomeDaClasse + " implements Serializable{\n" +
				"\n" +
				"	private static final long serialVersionUID = 1L;\n" +
				"\n" +
				"	@Id\n" +
				"	@GeneratedValue(strategy = GenerationType.AUTO)\n" +
				"	@Column(name = \"" + pk.getColum() + "\")\n" +
				"	private " + pk.getTipo() + " " + pk.getNome() + ";";
		for(Linha atr : linhas) {
			classe += "\n	\n	@Column(name = \"" + atr.getColum() + "\")\n	private " 
		+ atr.getTipo() + " " + atr.getNome() + ";";
		}
		classe += "	\n\n" +
				"	@Override\n" +
				"	public int hashCode() {\n" +
				"		final int prime = 31;\n" +
				"		int result = 1;\n" +
				"		result = prime * result + ((" + pk.getNome() + " == null) ? 0 : " + pk.getNome() + ".hashCode());\n" +
				"		return result;\n" +
				"	}\n" +
				"	@Override\n" +
				"	public boolean equals(Object obj) {\n" +
				"		if (this == obj)\n" +
				"			return true;\n" +
				"		if (obj == null)\n" +
				"			return false;\n" +
				"		if (getClass() != obj.getClass())\n" +
				"			return false;\n" +
				"		" + nomeDaClasse + " other = (" + nomeDaClasse + ") obj;\n" +
				"		if (" + pk.getNome() + " == null) {\n" +
				"			if (other." + pk.getNome() + " != null)\n" +
				"				return false;\n" +
				"		} else if (!" + pk.getNome() + ".equals(other." + pk.getNome() + "))\n" +
				"			return false;\n" +
				"		return true;\n" +
				"	}\n" +
				"}\n" +
				"\n";
		incluir(classe, caminhoProjetoRaiz + "\\model\\" + nomeDaClasse + ".java");
	}
	
	private static void incluir(String classe, String caminho){
        try {
            //Cria o arquivo
            FileWriter cria = new FileWriter(caminho, true);
            //Cria a caixa dagua
            BufferedWriter caixa = new BufferedWriter(cria);
            //Escreve no arqivo
            caixa.write(classe);
            //fecha o arquivo
            caixa.close();
            System.out.println(String.format("Criada a Classe %s", caminho));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	private static void addImport(String caminho) {
		imports.add(String.format("\nimport %s;", caminho));
	}
	
	private static String getTipo(String tipoBd) {
		if(tipoBd.contains("int") || tipoBd.contains("tinyint")) {
			return "Integer";
		}
		if(tipoBd.contains("decimal") || tipoBd.contains("float")) {
			return "Double";
		}
		if(tipoBd.contains("date")) {
			addImport("java.util.Date");
			return "Date";
		}
		return "String";
	}
	
	private static String getClassName(String nomeDaTabela) {
		String nomeDaClasse = (nomeDaTabela.charAt(0) + "").toUpperCase();
		nomeDaClasse = getCamelCase(nomeDaTabela, nomeDaClasse);
		return nomeDaClasse;
	}
	
	private static String getFieldName(String field) {
		String nomeDaClasse = (field.charAt(0) + "").toLowerCase();
		nomeDaClasse = getCamelCase(field, nomeDaClasse);
		return nomeDaClasse;
	}
	
	private static String getCamelCase(String field, String novo) {
		boolean underline = false;
		for (int i = 1; i < field.length(); i++) {
			String charr = field.charAt(i) + "";
			if(underline) {
				underline = false;
				charr = charr.toUpperCase();
			}
			else if(charr.equals("_")) {
				underline = true;
				continue;
			}
			novo += charr;
		}
		return novo;
	}
}
