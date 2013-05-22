import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import models.Group;
import models.Log;
import models.Message;
import models.User;
import org.apache.commons.io.FileUtils;
import play.*;
import play.db.DB;
import play.db.ebean.Transactional;
import utils.KeyPairGenerator;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Global extends GlobalSettings {

    @Override
    @Transactional
    public void onStart(Application app) {
        List<User> users = User.find.all();
        for (User u : users)
            u.delete();

        List<Group> groups = Group.find.all();
        for (Group g : groups)
            g.delete();

        List<Log> logs = Log.find.all();
        for (Log l : logs)
            l.delete();

        List<Message> messages = Message.find.all();
        for (Message m : messages)
            m.delete();

        Group g = new Group();
        g.setName("Administrador");
        g.save();

        System.out.println("Created admin group");

        User u = new User();
        u.setName("User");
        u.generateSalt();
        try {
            u.createPassword("BEBADO");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        u.setGroup(g);
        u.setUsername("user");
        try {
            u.setPublicKey(FileUtils.readFileToByteArray(new File("test/userpub")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        u.save();

        u = new User();
        u.setName("Administrador Maneiro");
        u.generateSalt();
        try {
            u.createPassword("MAFEVE");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        u.setGroup(g);
        u.setUsername("admin");

        g = new Group();
        g.setName("Usuário");
        g.save();

        System.out.println("Created user admin");

        try {
            KeyPairGenerator.generateKeyPair("admin", "senhalonga");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            u.setPublicKey(FileUtils.readFileToByteArray(new File("test/admin.pub")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        u.save();

        messages = new ArrayList<>();
        messages.add(new Message("Sistema iniciado.","1001"));
        messages.add(new Message("Sistema encerrado.","1002"));
        messages.add(new Message("Autenticação etapa 1 iniciada.","2001"));
        messages.add(new Message("Autenticação etapa 1 encerrada.","2002"));
        messages.add(new Message("Login name <login_name> identificado com acesso liberado.","2003"));
        messages.add(new Message("Login name <login_name> identificado com acesso bloqueado.","2004"));
        messages.add(new Message("Login name <login_name> não identificado.","2005"));
        messages.add(new Message("Autenticação etapa 2 iniciada para <login_name>.","3001"));
        messages.add(new Message("Autenticação etapa 2 encerrada para <login_name>.","3002"));
        messages.add(new Message("Senha pessoal verificada positivamente para <login_name>.","3003"));
        messages.add(new Message("Senha pessoal verificada negativamente para <login_name>.","3004"));
        messages.add(new Message("Primeiro erro da senha pessoal contabilizado para <login_name>.","3005"));
        messages.add(new Message("Segundo erro da senha pessoal contabilizado para <login_name>.","3006"));
        messages.add(new Message("Terceiro erro da senha pessoal contabilizado para <login_name>.","3007"));
        messages.add(new Message("Acesso do usuario <login_name> bloqueado pela autenticação etapa 2.","3008"));
        messages.add(new Message("Autenticação etapa 3 iniciada para <login_name>.","4001"));
        messages.add(new Message("Autenticação etapa 3 encerrada para <login_name>.","4002"));
        messages.add(new Message("Chave privada verificada positivamente para <login_name>.","4003"));
        messages.add(new Message("Primeiro erro da chave privada contabilizado para <login_name>.","4004"));
        messages.add(new Message("Segundo erro da chave privada contabilizado para <login_name>.","4005"));
        messages.add(new Message("Terceiro erro da chave privada contabilizado para <login_name>.","4006"));
        messages.add(new Message("Acesso do usuario <login_name> bloqueado pela autenticação etapa 3.","4007"));
        messages.add(new Message("Tela principal apresentada para <login_name>.","5001"));
        messages.add(new Message("Opção 1 do menu principal selecionada por <login_name>.","5002"));
        messages.add(new Message("Opção 2 do menu principal selecionada por <login_name>.","5003"));
        messages.add(new Message("Opção 3 do menu principal selecionada por <login_name>.","5004"));
        messages.add(new Message("Opção 4 do menu principal selecionada por <login_name>.","5005"));
        messages.add(new Message("Tela de cadastro apresentada para <login_name>.","6001"));
        messages.add(new Message("Botão cadastrar pressionado por <login_name>.","6002"));
        messages.add(new Message("Botão voltar de cadastrar para o menu principal pressionado por <login_name>.","6003"));
        messages.add(new Message("Tela de alteração apresentada para <login_name>.","7001"));
        messages.add(new Message("Botão alterar pressionado por <login_name>.","7002"));
        messages.add(new Message("Botão voltar de alterar para o menu principal pressionado por <login_name>.","7003"));
        messages.add(new Message("Tela de consulta apresentada para <login_name>.","8001"));
        messages.add(new Message("Botão voltar de consultar para o menu principal pressionado por <login_name>.","8002"));
        messages.add(new Message("Arquivo <arq_name> selecionado por <login_name> para decriptação.","8003"));
        messages.add(new Message("Arquivo <arq_name> decriptado com sucesso para <login_name>.","8004"));
        messages.add(new Message("Arquivo <arq_name> verificado com sucesso para <login_name>.","8005"));
        messages.add(new Message("Falha na decriptação do arquivo <arq_name> para <login_name>.","8006"));
        messages.add(new Message("Falha na verificação do arquivo <arq_name> para <login_name>.","8007"));
        messages.add(new Message("Tela de saída apresentada para <login_name>.","9001"));
        messages.add(new Message("Botão sair pressionado por <login_name>.","9002"));
        messages.add(new Message("Botão voltar de sair para o menu principal pressionado por <login_name>.","9003"));

        Message.createAll(messages);

        Log.log("1001");

        /*
        try {
            Process process = Runtime.getRuntime().exec( new String[] { "./create-applet-jar.sh" } );
            process.waitFor();
            process.destroy();
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        */

    }

    @Override
    public void onStop(Application app) {
        Log.log("1002");
    }

}