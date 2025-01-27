package com.KangreSystem.models.imp;

import java.util.Date;

import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import com.KangreSystem.models.service.ISimpleMailService;

@Service
public class SimpleMailServiceImp implements ISimpleMailService {

	@Autowired
	private JavaMailSender javaMailSender;
	
	@Override
	public void send(String nombre, String correoTo, String asunto, String mensaje) throws Exception {
		
			MimeMessage mimeMessage = javaMailSender.createMimeMessage();
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
			mimeMessageHelper.setFrom(nombre);
			mimeMessageHelper.setTo(correoTo);
			mimeMessageHelper.setSubject(asunto);
			mimeMessageHelper.setText(mensaje, true);
			mimeMessageHelper.setSentDate(new Date());
			javaMailSender.send(mimeMessage);
	}

}
