package com.curso.ecommerce.controller;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.curso.ecommerce.model.Orden;
import com.curso.ecommerce.model.Producto;
import com.curso.ecommerce.model.Usuario;
import com.curso.ecommerce.service.IOrdenService;
import com.curso.ecommerce.service.IUsuarioService;
import com.curso.ecommerce.service.ProductoService;

@Controller
@RequestMapping("/admin")
public class AdministradorController {

	@Autowired
	private ProductoService productoService;
	
	@Autowired
	private IUsuarioService usuarioService;
	
	@Autowired
	private IOrdenService ordensService;
	
	private Logger logg= LoggerFactory.getLogger(AdministradorController.class);

	@GetMapping("")
	public String home(Model model, Usuario usuario, HttpSession session) {
		try {
		    int id = (int) session.getAttribute("idusuario");
		    Optional<Usuario> user = usuarioService.findById(id);
		    List<Producto> productos = productoService.findAll();
		    model.addAttribute("productos", productos);

		    if (user.isPresent()) {
		        if (user.get().getTipo().equals("ADMIN")) {
		            //session
		            model.addAttribute("sesion", session.getAttribute("idusuario"));
		            return "administrador/home";
		        }
		    } else {
		        return "redirect:/usuario/login";
		    }
		} catch (Exception e) {
		    // Manejo de la excepción (puedes imprimir el mensaje de error, redirigir a una página de error, etc.)
		    return "redirect:/usuario/login";
		}
		return "redirect:/usuario/login";
		
	}
	
	@GetMapping("/usuario")
	public String usuarios(Model model, HttpSession session) {
	    try {
	        int id = (int) session.getAttribute("idusuario");
	        Optional<Usuario> user = usuarioService.findById(id);

	        if (user.isPresent()) {
	            if (user.get().getTipo().equals("ADMIN")) {
	                // Puedes seguir con la lógica específica para la página de usuarios administradores
	                model.addAttribute("usuarios", usuarioService.findAll());
	                return "administrador/usuarios";
	            } else {
	                // Redirigir si el usuario no es un administrador
	                return "redirect:/usuario/login";
	            }
	        } else {
	            // Redirigir si no hay sesión o el usuario no está autenticado
	            return "redirect:/usuario/login";
	        }
	    } catch (Exception e) {
	        // Manejo de la excepción (puedes imprimir el mensaje de error, redirigir a una página de error, etc.)
	        return "redirect:/usuario/login";
	    }
	}
	
	@GetMapping("/ordenes")
	public String ordenes(Model model, HttpSession session) {
	    try {
	        int id = (int) session.getAttribute("idusuario");
	        Optional<Usuario> user = usuarioService.findById(id);

	        if (user.isPresent()) {
	            if (user.get().getTipo().equals("ADMIN")) {
	                model.addAttribute("ordenes", ordensService.findAll());
	                return "administrador/ordenes";
	            } else {
	                return "redirect:/usuario/login";
	            }
	        } else {
	            return "redirect:/usuario/login";
	        }
	    } catch (Exception e) {
	        return "redirect:/usuario/login";
	    }
	}

	@GetMapping("/detalle/{id}")
	public String detalle(Model model, @PathVariable Integer id, HttpSession session) {
	    try {
	        int userId = (int) session.getAttribute("idusuario");
	        Optional<Usuario> user = usuarioService.findById(userId);

	        if (user.isPresent() && user.get().getTipo().equals("ADMIN")) {
	            logg.info("Id de la orden {}", id);
	            Optional<Orden> ordenOptional = ordensService.findById(id);

	            if (ordenOptional.isPresent()) {
	                Orden orden = ordenOptional.get();
	                model.addAttribute("detalles", orden.getDetalle());
	                return "administrador/detalleorden";
	            } else {
	                // Puedes manejar el caso en que la orden no existe
	                // Por ejemplo, redirigiendo a una página de error o mostrando un mensaje apropiado.
	                return "redirect:/error";
	            }
	        } else {
	            return "redirect:/usuario/login";
	        }
	    } catch (Exception e) {
	        return "redirect:/usuario/login";
	    }
	}
	
	
}
