package com.curso.ecommerce.controller;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.curso.ecommerce.model.Producto;
import com.curso.ecommerce.model.Usuario;
import com.curso.ecommerce.service.IUsuarioService;
import com.curso.ecommerce.service.ProductoService;
import com.curso.ecommerce.service.UploadFileService;
import com.curso.ecommerce.service.UsuarioServiceImpl;

@Controller
@RequestMapping("/producto")
public class ProductoController {
	
	private final Logger LOGGER = LoggerFactory.getLogger(ProductoController.class);
	
	@Autowired
	private ProductoService productoService;
	
	@Autowired
	private IUsuarioService usuarioService;
	
	@Autowired
	private UploadFileService upload;
	
	@GetMapping("")
	public String show(Model model) {
		model.addAttribute("productos", productoService.findAll());
		return "productos/show";
	}
	
	@GetMapping("/crear")
	public String create() {
		return "productos/crear";
	}
	
	@PostMapping("/guardar")
	public String save(Producto producto, @RequestParam("img") MultipartFile file, HttpSession session) throws IOException {
	    LOGGER.info("Este es el objeto producto {}", producto);

	    // Check if "idusuario" attribute is present in the session
	    Object idUsuarioAttribute = session.getAttribute("idusuario");
	    if (idUsuarioAttribute != null) {
	        // If present, convert it to an Integer
	        Integer idUsuario = Integer.parseInt(idUsuarioAttribute.toString());

	        // Retrieve the user based on the idUsuario
	        Usuario u = usuarioService.findById(idUsuario).orElse(null);

	        if (u != null) {
	            producto.setUsuario(u);

	            //imagen
	            if (producto.getId() == null) { // cuando se crea un producto
	                String nombreImagen = upload.saveImage(file);
	                producto.setImagen(nombreImagen);
	            } else {
	                // Handle the case when the product already exists (if needed)
	            }

	            productoService.save(producto);
	            return "redirect:/admin";
	        } else {
	            // Handle the case when the user is not found
	            // You might want to log a warning or handle it in some appropriate way
	        }
	    } else {
	        // Handle the case when "idusuario" attribute is not present in the session
	        // You might want to log a warning or handle it in some appropriate way
	    }

	    return "redirect:/producto"; // Redirect to login page or another appropriate page
	}
	
	@GetMapping("/edit/{id}")
	public String edit(@PathVariable Integer id, Model model) {
		Producto producto= new Producto();
		Optional<Producto> optionalProducto=productoService.get(id);
		producto= optionalProducto.get();
		
		LOGGER.info("Producto buscado: {}",producto);
		model.addAttribute("producto", producto);
		
		return "productos/edit";
	}
	
	@PostMapping("/update")
	public String update(Producto producto, @RequestParam("img") MultipartFile file ) throws IOException {
		Producto p= new Producto();
		p=productoService.get(producto.getId()).get();
		if (file.isEmpty()) {
			producto.setImagen(p.getImagen());
		}else {
			if (!p.getImagen().equals("default.jpg")) {
				upload.deleteImage(p.getImagen());
			}
			String nombreImagen= upload.saveImage(file);
			producto.setImagen(nombreImagen);
		}
		producto.setUsuario(p.getUsuario());
		productoService.update(producto);		
		return "redirect:/producto";
	}
	
	@GetMapping("/delete/{id}")
	public String delete(@PathVariable Integer id) {
		Producto p = new Producto();
		p=productoService.get(id).get();
		if (!p.getImagen().equals("default.jpg")) {
			upload.deleteImage(p.getImagen());
		}
		
		productoService.delete(id);
		return "redirect:/producto";
	}
	
	
}
