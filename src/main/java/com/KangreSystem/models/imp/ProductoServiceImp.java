package com.KangreSystem.models.imp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.StoredProcedureQuery;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.KangreSystem.models.entity.CategoriaProducto;
import com.KangreSystem.models.entity.Producto;
import com.KangreSystem.models.repository.CategoriaProductoRepository;
import com.KangreSystem.models.repository.ProductoRepository;
import com.KangreSystem.models.service.IProductoService;

@Service
public class ProductoServiceImp implements IProductoService {
	
	@Autowired
	private ProductoRepository productoRepository;
	
	@Autowired
	private CategoriaProductoRepository categoriaRepository;
	
	@Autowired
	private EntityManager entityManager;

	@SuppressWarnings("unchecked")
	@Override
	public List<Producto> findAllByViaProd() {
		StoredProcedureQuery storedProcedureQuery = this.entityManager.createNamedStoredProcedureQuery("getAllProductos");
		storedProcedureQuery.execute();
		return storedProcedureQuery.getResultList();
	}

	@Override
	public void guardar(Producto producto) {
		producto.setNombre(producto.getNombre().toUpperCase());
		producto.setDetalle(producto.getDetalle().toUpperCase());
		productoRepository.save(producto);
	}

	@Override
	public Producto buscarPorId(Long idProducto) {
		return productoRepository.findById(idProducto).orElse(null);
	}

	@Override
	public void eliminar(Long idProducto) {
		productoRepository.deleteById(idProducto);
	}

	@Override
	public boolean existePorId(Long idProducto) {
		return productoRepository.existsById(idProducto);
	}

	@Override
	public boolean checkImgIsValid(MultipartFile file) {
		String extension = FilenameUtils.getExtension(file.getOriginalFilename());
		int _5MB = 5120;
		long size = (file.getSize() / 1024); 
		
		if ((extension.equals("png") || extension.equals("jpg") || extension.equals("jpeg")) && (size <= _5MB)) {
			return true;
		}
		
		return false;
	}

	@Override
	public boolean existePorNombre(String nombre) {
		return productoRepository.existsByNombre(nombre);
	}

	@Override
	public Producto buscarPorNombre(String nombre) {
		return productoRepository.findByNombre(nombre);
	}

	@Override
	public boolean validProducto(Producto oldProducto, Producto newProducto) {
		boolean existeNombre = productoRepository.existsByNombre(newProducto.getNombre());
		if (!existeNombre || newProducto.getNombre().toUpperCase().equals(oldProducto.getNombre().toUpperCase())) {
			return true;
		}
		return false;
	}

	@Override
	public List<Producto> filtrar(CategoriaProducto categoria, String estado) {
		if (categoria != null && !estado.isEmpty()) {
			return productoRepository.findByCategoriaAndEstado(categoria, estado);
		}
		if (categoria != null && estado.isEmpty()) {
			return productoRepository.findByCategoria(categoria);
		}
		if (categoria == null && !estado.isEmpty()) {
			return productoRepository.findByEstado(estado);
		}
		return (List<Producto>) productoRepository.findAll();
	}

	@Override
	public List<Producto> buscarProductosPorNombre(String nombre) {
		return productoRepository.findByNombreContaining(nombre);
	}

	@Override
	public List<Producto> buscarPorCategoria(CategoriaProducto categoria) {
		return productoRepository.findByCategoria(categoria);
	}

	@Override
	public List<Producto> buscarPorEstado(String estado) {
		return productoRepository.findByEstado(estado);
	}

	@Override
	public List<Producto> buscarPorCategoriaEstado(CategoriaProducto categoria, String estado) {
		return productoRepository.findByCategoriaAndEstado(categoria, estado);
	}

	@Override
	public boolean saveDataFromUploadFile(MultipartFile file) {
		boolean isFlag = false;
		String extension = FilenameUtils.getExtension(file.getOriginalFilename());
		if (extension.equalsIgnoreCase("xls") || extension.equalsIgnoreCase("xlsx")) {
			isFlag = readDataFromExcel(file);
		}
		return isFlag;
	}

	private boolean readDataFromExcel(MultipartFile file) {
		Workbook workbook = getWorkBook(file);
		Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rows = sheet.iterator();
		CategoriaProducto categoria = null;
		
		rows.next();
		
		while (rows.hasNext()) {
			
			try {
				Row row = rows.next();
				Producto producto = new Producto();
				
				if (row.getCell(0).getCellType() == Cell.CELL_TYPE_STRING) {
					producto.setNombre(row.getCell(0).getStringCellValue());
					if (productoRepository.existsByNombre(row.getCell(0).getStringCellValue())) {
						return false;
					}
				}
				
				if (row.getCell(1).getCellType() == Cell.CELL_TYPE_STRING) {
					producto.setDetalle(row.getCell(1).getStringCellValue());
				}
				
				if (row.getCell(2).getCellType() == Cell.CELL_TYPE_NUMERIC) {
					String price = NumberToTextConverter.toText(row.getCell(2).getNumericCellValue());
					Long precio = Long.parseLong(price);
					producto.setPrecio(precio);
				}	else if (row.getCell(2).getCellType() == Cell.CELL_TYPE_STRING) {
					String price = NumberToTextConverter.toText(row.getCell(2).getNumericCellValue());
					Long precio = Long.parseLong(price);
					producto.setPrecio(precio);
				}
				
				if (row.getCell(3).getCellType() == Cell.CELL_TYPE_NUMERIC) {
					String id = NumberToTextConverter.toText(row.getCell(3).getNumericCellValue());
					Long idCategoria = Long.parseLong(id);
					categoria = categoriaRepository.findById(idCategoria).orElse(null);
					
					if (categoria == null) {
						return false;
					}
					
					producto.setCategoria(categoria);
				}	else if (row.getCell(3).getCellType() == Cell.CELL_TYPE_STRING) {
					producto.setCategoria(categoria);
				}
				
				producto.setEstado("ACTIVO");
				producto.setCantVendida(0);
				producto.setFoto("producto.png");
				
				productoRepository.save(producto);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			
		}
		
		return true;
	}

	private Workbook getWorkBook(MultipartFile file) {
		Workbook workbook = null;
		String extension = FilenameUtils.getExtension(file.getOriginalFilename());
		
		try {
			if (extension.equalsIgnoreCase("xlsx")) {
				workbook = new XSSFWorkbook(file.getInputStream());
			} else if (extension.equalsIgnoreCase("xls")){
				workbook = new HSSFWorkbook(file.getInputStream());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return workbook;
	}

	@Override
	public Long totalProductos() {
		return productoRepository.count();
	}

	@Override
	public List<Producto> cincoMasVendidos() {
		List<Producto> productos = productoRepository.orderByCantVendidaDesc();
		List<Producto> cincoMasVendidos = new ArrayList<>();
		
		for (Producto producto : productos) {
			if (cincoMasVendidos.size() < 5) {
				cincoMasVendidos.add(producto);
			}
		}
		
		return cincoMasVendidos;
	}

	@Override
	public List<Producto> cincoMenosVendidos() {
		List<Producto> productos = productoRepository.orderByCantVendidaAsc();
		List<Producto> cincoMenosVendidos = new ArrayList<>();
		
		for (Producto producto : productos) {
			if (cincoMenosVendidos.size() < 5) {
				cincoMenosVendidos.add(producto);
			}
		}
		
		return cincoMenosVendidos;
	}

}
