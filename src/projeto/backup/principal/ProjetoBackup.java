package projeto.backup.principal;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.ArrayList;
import java.util.List;

import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
//import java.util.Properties;

import java.util.logging.Level;
import java.util.logging.Logger;

//import javax.mail.Address;
//import javax.mail.Message;
//import javax.mail.MessagingException;
//import javax.mail.PasswordAuthentication;

//import javax.mail.Session;
//import javax.mail.Transport;
//import javax.mail.internet.AddressException;
//import javax.mail.internet.InternetAddress;
//import javax.mail.internet.MimeMessage;

import projeto.backup.email.EmailPrincipal;


//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.time.format.FormatStyle;
//import org.apache.log4j.Logger;


public class ProjetoBackup {

    //atributo especifico para saber o tamanho total de todos os arquivos
    public static List<Long> tamanhoArquivos = new ArrayList<Long>();

    public static String LOGINFO(String informacao, String dataLocal) {
        Path path = Paths.get("C:\\log\\");
        try {
            Files.createDirectories(path);
        } catch (IOException ex) {
            Logger.getLogger(ProjetoBackup.class.getName()).log(Level.SEVERE, null, ex.getMessage());
        }
        File arquivo = new File("C:\\log\\arquivo.log");
        List<String> log = new ArrayList<String>();
        if (!arquivo.exists()) {
            try {
                arquivo.createNewFile();
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(ProjetoBackup.class.getName()).log(Level.SEVERE, null, ex.getMessage());
            }
        }
        log.add(informacao + " " + dataLocal);
        try {
            Files.write(Paths.get(arquivo.getPath()), log, StandardOpenOption.APPEND);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(ProjetoBackup.class.getName()).log(Level.SEVERE, null, ex.getMessage());
        }
        return log.get(0);
    }

//responsavel por criar o respectivo log no sistema
//    public static Logger logPrincipal(Class classe) {
//        if (classe != null) {
//            Logger LOG = Logger.getLogger(classe);
//
//            return LOG;
//        }
//        return null;
//    }
    //validacao com a data e horas correspondente
    public static boolean DATAEHVALIDO(String format) {
        return format.matches("dd-MM-yyyy HH-mm-ss") || format.matches("dd/MM/yyyy HH:mm:ss");
    }

    //retornar a data de Hj no Formato do tipo String
    public static String DATATOSTR(String format) {
        String retorno = null;

        if (DATAEHVALIDO(format) == true) {
            SimpleDateFormat formater = new SimpleDateFormat(format);
            formater.setLenient(false);
            retorno = formater.format(Calendar.getInstance().getTime());
        }
        return retorno;
    }

    public static String CRIARDIRETORIO(String parteASerCriada, String pastaASerCriada) {
        StringBuffer localDiretorio = new StringBuffer();
        //local do arquivo a ser criado
        localDiretorio.append(parteASerCriada).append(pastaASerCriada);
        try {

            Path path = Paths.get(localDiretorio.toString());
            Path criarArquivos = Files.createDirectories(path);

            if (pastaASerCriada.equalsIgnoreCase(criarArquivos.getFileName().toString())) {
                LOGINFO(">>>>NOVO DIRETORIO CRIADO>>>>>" + localDiretorio.toString() + ">>>CRIANDO ARQUIVOS...", DATATOSTR("dd/MM/yyyy HH:mm:ss"));
            }

        } catch (Exception ex) {
            LOGINFO(">>>>ERROR (CRIARDIRETORIO) E SISTEMA ENCERRADO!!!", DATATOSTR("dd/MM/yyyy HH:mm:ss") + " " + ex.getMessage());
            System.exit(0);
        }
        return localDiretorio.toString();
    }

    /*
    *https://stackoverflow.com/questions/9292954/how-to-make-a-copy-of-a-file-in-android/9293885#9293885
     */
    public static void COPYFILEORDIRECTORY(String srcDir, String dstDir) {

        try {
            File src = new File(srcDir);
            File dst = new File(dstDir, src.getName());

            if (src.isDirectory()) {

                String files[] = src.list();
                int filesLength = files.length;
                for (int i = 0; i < filesLength; i++) {
                    String src1 = (new File(src, files[i]).getPath());
                    String dst1 = dst.getPath();
                    COPYFILEORDIRECTORY(src1, dst1);

                }
            } else {
                COPYFILE(src, dst);
            }
        } catch (Exception ex) {
            LOGINFO(">>>>ERROR (COPYFILEORDIRECTORY)", DATATOSTR("dd/MM/yyyy HH:mm:ss") + " " + ex.getMessage());

        }
    }

    /**
     * https://stackoverflow.com/questions/3758606/how-can-i-convert-byte-size-into-a-human-readable-format-in-java
     * *
     */
    public static void COPYFILE(File sourceFile, File destFile) {
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }

        if (!destFile.exists()) {
            try {
                destFile.createNewFile();
            } catch (IOException ex) {
                LOGINFO(">>>>ERROR (COPYFILE)", DATATOSTR("dd/MM/yyyy HH:mm:ss") + " " + ex.getMessage());
            }
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());

            tamanhoArquivos.add(source.size());
        } catch (FileNotFoundException ex) {
            LOGINFO(">>>>ERROR (COPYFILE)", DATATOSTR("dd/MM/yyyy HH:mm:ss") + " " + ex.getMessage());
        } catch (IOException ex) {
            LOGINFO(">>>>ERROR (COPYFILE)", DATATOSTR("dd/MM/yyyy HH:mm:ss") + " " + ex.getMessage());
        } finally {
            if (source != null) {
                try {
                    source.close();
                } catch (IOException ex) {
                    LOGINFO(">>>>ERROR (COPYFILE)", DATATOSTR("dd/MM/yyyy HH:mm:ss") + " " + ex.getMessage());
                }
            }
            if (destination != null) {
                try {
                    destination.close();
                } catch (IOException ex) {
                    LOGINFO(">>>>ERROR (COPYFILE)", DATATOSTR("dd/MM/yyyy HH:mm:ss") + " " + ex.getMessage());
                }
            }
        }
    }

    /**
     * https://stackoverflow.com/questions/3758606/how-can-i-convert-byte-size-into-a-human-readable-format-in-java
     */
    public static String formatSize(long v) {
        if (v < 1024) {
            return v + " B";
        }
        int z = (63 - Long.numberOfLeadingZeros(v)) / 10;
        return String.format("%.1f %sB", (double) v / (1L << (z * 10)), " KMGTPE".charAt(z));
    }

//    public static void ENVIODEEMAIL() {
//        // [START simple_example]
//        Properties props = new Properties();
//
//        props.put("mail.smtp.starttls.enable", "true");
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.host", "smtp.googlemail.com");
//        props.put("mail.smtp.port", "587");
//
//        Session session = Session.getInstance(props,
//                new javax.mail.Authenticator() {
//            @Override
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication("",
//                        "");
//            }
//        });
//
//        /**
//         * Ativa Debug para sessão
//         */
//        session.setDebug(true);
//
//        try {
//            Message msg = new MimeMessage(session);
//            msg.setFrom(new InternetAddress(""));
//
//            Address[] usuariosEmail = InternetAddress.parse("");
//
//            msg.addRecipients(Message.RecipientType.TO, usuariosEmail);
//            msg.setSubject("Teste Exemplo");
//            msg.setText("Isso e um teste....");
//
//            msg.setContent(
//                    "<h1> Tes </h1>",
//                    "text/html");
//
//            Transport.send(msg);
//        } catch (AddressException ex) {
//            LOGINFO(">>>>ERROR (ENVIODEEMAIL())", DATATOSTR("dd/MM/yyyy HH:mm:ss") + " " + ex.getMessage());
//        } catch (MessagingException ex) {
//            LOGINFO(">>>>ERROR (ENVIODEEMAIL())", DATATOSTR("dd/MM/yyyy HH:mm:ss") + " " + ex);
//        }
//        // [END simple_example]
//
//        // [END simple_example]
//    }
    public static void main(String[] args) {

        EmailPrincipal emailBackup = new EmailPrincipal();

        System.out.println("INICIANDO BACKUP...");
        LOGINFO("****BACKUP****", DATATOSTR("dd/MM/yyyy HH:mm:ss"));
        //ENVIODEEMAIL();
        emailBackup.cadastrarConectadoEmail("email from", "Backup Iniciando" + LOGINFO("****BACKUP****", DATATOSTR("dd/MM/yyyy HH:mm:ss")));
        String origemDados = "\\\\local na rede\\dados especificos\\";
        String destinoDados = null;
        String parteASerCriada = "D:\\Backup\\BackupServerAD\\";

        String criarDiretorio = DATATOSTR("dd-MM-yyyy HH-mm-ss");
        System.out.println("NOME DA PASTA>>>> " + criarDiretorio);
        if (criarDiretorio != null) {
            destinoDados = CRIARDIRETORIO(parteASerCriada, criarDiretorio);
        }

        COPYFILEORDIRECTORY(origemDados, destinoDados);

        long tamanho = 0;

        tamanho = tamanhoArquivos.stream().map(tamanhoArquivo -> tamanhoArquivo).reduce(tamanho, (accumulator, _item) -> accumulator + _item); //t += formatSize(tamanhoArquivo);

        if (tamanho > 0 && emailBackup.validarEmailPessoa("email from")) {
            emailBackup.cadastrarConectadoEmail("email from", LOGINFO(">>>>COPIA EFETUADA COM SUCESSO AO DIRETORIO>>>>" + destinoDados + ">>>>E O TAMANHO>>>>" + formatSize(tamanho) + " ", DATATOSTR("dd/MM/yyyy HH:mm:ss")));
        } else {
            emailBackup.cadastrarConectadoEmail("email from", LOGINFO(">>>>COPIA NAO EFETUADA AO DIRETORIO>>>>" + destinoDados + ">>>>E O TAMANHO>>>>" + formatSize(tamanho) + " ", DATATOSTR("dd/MM/yyyy HH:mm:ss")));
        }

    }
}
