package com.KangreSystem.models.imp;

import java.util.Iterator;
import java.util.List;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.KangreSystem.models.entity.CategoriaProveedor;
import com.KangreSystem.models.entity.Proveedor;
import com.KangreSystem.models.repository.CategoriaProveedorRepository;
import com.KangreSystem.models.repository.ProveedorRepository;
import com.KangreSystem.models.service.IProveedorService;

@Service
@Transactional
public class ProveedorServiceImp implements IProveedorService {

	@Autowired
	private ProveedorRepository proveedorRepository;
	
	@Autowired
	private CategoriaProveedorRepository categoriaRepository;
	
	@Override
	public List<Proveedor> buscarPorCategoriaCiudadEnabled(CategoriaProveedor categoria, String ciudad, String estado) {
		if (categoria != null && ciudad.isEmpty() && estado.isEmpty()) {
			return proveedorRepository.findByCategoria(categoria);
		}
		if (categoria == null && !ciudad.isEmpty() && estado.isEmpty()) {
			return proveedorRepository.findByCiudad(ciudad);
		}
		if (categoria == null && ciudad.isEmpty() && !estado.isEmpty()) {
			return proveedorRepository.findByEnabled(Boolean.parseBoolean(estado));
		}
		if (categoria != null && !ciudad.isEmpty() && !estado.isEmpty()) {
			return proveedorRepository.findByCategoriaAndCiudadAndEnabled(categoria, ciudad, Boolean.parseBoolean(estado));
		}
		if (categoria != null && !ciudad.isEmpty() && estado.isEmpty()) {
			return proveedorRepository.findByCategoriaAndCiudad(categoria, ciudad);
		}
		if (categoria != null && ciudad.isEmpty() && !estado.isEmpty()) {
			return proveedorRepository.findByCategoriaAndEnabled(categoria, Boolean.parseBoolean(estado));
		}
		if (categoria == null && !ciudad.isEmpty() && !estado.isEmpty()) {
			return proveedorRepository.findByCiudadAndEnabled(ciudad, Boolean.parseBoolean(estado));
		}
		return (List<Proveedor>) proveedorRepository.findAll();
	}
	
	
	@Override
	public List<Proveedor> listar() {
		return (List<Proveedor>) proveedorRepository.findAll();
	}

	@Override
	public Proveedor buscarPorId(Long idProveedor) {
		return proveedorRepository.findById(idProveedor).orElse(null);
	}

	@Override
	public void guardar(Proveedor proveedor) throws Exception {
		
		if (!existsProveedorByNitAndNombre(proveedor.getNitProveedor(), proveedor.getNombre())) {
			proveedor.setNombre(proveedor.getNombre().toUpperCase());
			proveedor.setEmail(proveedor.getEmail().toUpperCase());
			proveedor.setDireccion(proveedor.getDireccion().toUpperCase());
			proveedorRepository.save(proveedor);
		}
		
	}

	@Override
	public void eliminar(Long idProveedor) {
		proveedorRepository.deleteById(idProveedor);
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
		
		rows.next();
		
		while (rows.hasNext()) {
			
			try {
				Row row = rows.next();
				Proveedor proveedor = new Proveedor();
				
				if (row.getCell(0).getCellType() == Cell.CELL_TYPE_NUMERIC) {
					String nitProveedor = NumberToTextConverter.toText(row.getCell(0).getNumericCellValue());
					
					if (proveedorRepository.existsByNitProveedor(nitProveedor)) {
						return false;
					}
					
					proveedor.setNitProveedor(nitProveedor);
				}	else if (row.getCell(0).getCellType() == Cell.CELL_TYPE_STRING) {
					
					if (proveedorRepository.existsByNitProveedor(row.getCell(0).getStringCellValue())) {
						return false;
					}
					
					proveedor.setNitProveedor(row.getCell(0).getStringCellValue());
				}
				
				
				
				if (row.getCell(1).getCellType() == Cell.CELL_TYPE_STRING) {
					proveedor.setNombre(row.getCell(1).getStringCellValue());
				}
				
				if (row.getCell(2).getCellType() == Cell.CELL_TYPE_NUMERIC) {
					String telefono = NumberToTextConverter.toText(row.getCell(2).getNumericCellValue());
					proveedor.setTelefono(telefono);
					
				}	else if (row.getCell(2).getCellType() == Cell.CELL_TYPE_STRING) {
					proveedor.setTelefono(row.getCell(2).getStringCellValue());
				}
				
				if (row.getCell(3).getCellType() == Cell.CELL_TYPE_NUMERIC) {
					String celular = NumberToTextConverter.toText(row.getCell(3).getNumericCellValue());
					proveedor.setCelular(celular);
					
				}	else if (row.getCell(3).getCellType() == Cell.CELL_TYPE_STRING) {
					proveedor.setCelular(row.getCell(3).getStringCellValue());
				}
				
				if (row.getCell(4).getCellType() == Cell.CELL_TYPE_STRING) {
					proveedor.setEmail(row.getCell(4).getStringCellValue());
				}
				
				if (row.getCell(5).getCellType() == Cell.CELL_TYPE_STRING) {
					proveedor.setCiudad(row.getCell(5).getStringCellValue());
				}
				
				if (row.getCell(6).getCellType() == Cell.CELL_TYPE_STRING) {
					proveedor.setDireccion(row.getCell(6).getStringCellValue());
				}
				
				if (row.getCell(7).getCellType() == Cell.CELL_TYPE_NUMERIC) {
					
					String id = NumberToTextConverter.toText(row.getCell(7).getNumericCellValue());
					Long idCategoria = Long.parseLong(id);
					
					CategoriaProveedor categoria = categoriaRepository.findById(idCategoria).orElse(null);
					proveedor.setCategoria(categoria);
					
				}	else if (row.getCell(7).getCellType() == Cell.CELL_TYPE_STRING) {
					String id =  row.getCell(7).getStringCellValue();
					Long idCategoria = Long.parseLong(id);
					CategoriaProveedor categoria = categoriaRepository.findById(idCategoria).orElse(null);
					proveedor.setCategoria(categoria);
				}
				proveedor.setTipoArchivo(FilenameUtils.getExtension(file.getOriginalFilename()));
				proveedor.setEnabled(true);
				proveedor.setLogo("proveedor.png");
				
				proveedorRepository.save(proveedor);
			} catch (Exception e) {
				System.out.println("Se va a la monda");
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
	public void eliminarTodo() {
		proveedorRepository.deleteAll();
	}
	
	@Override
	public boolean existsProveedorByNitAndNombre(String nitProveedor, String nombre) throws Exception {
		
		if (proveedorRepository.existsByNitProveedor(nitProveedor) || proveedorRepository.existsByNombre(nombre)) {
			throw new Exception("El proveedor ya esta registrado");
		}
		
		return false;
	}
	
	


	@Override
	public List<Proveedor> buscarPorNitProveedor(String nitProveedor) {
		return proveedorRepository.findByNitProveedorContaining(nitProveedor);
	}


	@Override
	public List<Proveedor> buscarPorCategoria(CategoriaProveedor categoria) {
		return proveedorRepository.findByCategoria(categoria);
	}


	@Override
	public List<Proveedor> buscarPorCiudad(String ciudad) {
		return proveedorRepository.findByCiudad(ciudad);
	}


	@Override
	public List<Proveedor> buscarPorEnabled(boolean enabled) {
		return proveedorRepository.findByEnabled(enabled);
	}

	@Override
	public List<Proveedor> buscarPorCategoriaCiudad(CategoriaProveedor categoria, String ciudad) {
		return proveedorRepository.findByCategoriaAndCiudad(categoria, ciudad);
	}


	@Override
	public List<Proveedor> buscarPorCategoriaEnabled(CategoriaProveedor categoria, boolean enabled) {
		return proveedorRepository.findByCategoriaAndEnabled(categoria, enabled);
	}


	@Override
	public List<Proveedor> buscarPorCiudadEnabled(String ciudad, boolean enabled) {
		return proveedorRepository.findByCiudadAndEnabled(ciudad, enabled);
	}


	@Override
	public boolean checkImgIsValid(MultipartFile file){
		String extension = FilenameUtils.getExtension(file.getOriginalFilename());
		int _5MB = 5120;
		long size = (file.getSize() / 1024); 
		
		if ((extension.equals("png") || extension.equals("jpg") || extension.equals("jpeg")) && (size <= _5MB)) {
			return true;
		}
		
		return false;
	}


	@Override
	public Long totalProveedores() {
		return proveedorRepository.count();
	}


	@Override
	public Proveedor buscarProveedorPorNit(String nitProveedor) {
		return proveedorRepository.findByNitProveedor(nitProveedor);
	}

}
