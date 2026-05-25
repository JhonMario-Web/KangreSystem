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
import org.springframework.web.multipart.MultipartFile;
import com.KangreSystem.models.entity.Ingrediente;
import com.KangreSystem.models.entity.Insumo;
import com.KangreSystem.models.entity.Producto;
import com.KangreSystem.models.repository.IngredienteRepository;
import com.KangreSystem.models.repository.InsumoRepository;
import com.KangreSystem.models.repository.ProductoRepository;
import com.KangreSystem.models.service.IIngredienteService;

@Service
public class IngredienteServiceImp implements IIngredienteService {
	
	@Autowired
	private IngredienteRepository ingredienteRepo;
	
	@Autowired
	private ProductoRepository productoRepository;
	
	@Autowired
	private InsumoRepository insumoRepository;

	@Override
	public List<Ingrediente> listar() {
		return (List<Ingrediente>) ingredienteRepo.findAll();
	}

	@Override
	public void guardar(Ingrediente ingrediente) {
		ingredienteRepo.save(ingrediente);
	}

	@Override
	public void eliminar(Long idIngrediente) {
		ingredienteRepo.deleteById(idIngrediente);
	}

	@Override
	public Ingrediente buscarPorId(Long idIngrediente) {
		return ingredienteRepo.findById(idIngrediente).orElse(null);
	}

	@Override
	public void guardarTodo(List<Ingrediente> ingredientes) {
		ingredienteRepo.saveAll(ingredientes);
	}

	@Override
	public List<Ingrediente> buscarPorProducto(Producto producto) {
		return ingredienteRepo.findByProducto(producto);
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
		Insumo insumo = null;
		Producto producto = null;
		
		rows.next();
		
		while (rows.hasNext()) {
			
			try {
				Row row = rows.next();
				Ingrediente ingrediente = new Ingrediente();
				
				if (row.getCell(0).getCellType() == Cell.CELL_TYPE_NUMERIC) {
					String id = NumberToTextConverter.toText(row.getCell(0).getNumericCellValue());
					Long idProducto = Long.parseLong(id);
					producto = productoRepository.findById(idProducto).orElse(null);
					
					if (producto == null) {
						return false;
					}
					
					ingrediente.setProducto(producto);
				}	else if (row.getCell(0).getCellType() == Cell.CELL_TYPE_STRING) {
					ingrediente.setProducto(producto);
				}
				
				if (row.getCell(1).getCellType() == Cell.CELL_TYPE_NUMERIC) {
					String id = NumberToTextConverter.toText(row.getCell(1).getNumericCellValue());
					Long idInsumo = Long.parseLong(id);
					insumo = insumoRepository.findById(idInsumo).orElse(null);
					
					if (insumo == null) {
						return false;
					}
					
					ingrediente.setInsumo(insumo);
				}	else if (row.getCell(1).getCellType() == Cell.CELL_TYPE_STRING) {
					ingrediente.setInsumo(insumo);
				}
				
				if (row.getCell(2).getCellType() == Cell.CELL_TYPE_NUMERIC) {
					String cant = NumberToTextConverter.toText(row.getCell(2).getNumericCellValue());
					Float cantidad = Float.parseFloat(cant);
					ingrediente.setCantidad(cantidad);
				}	else if (row.getCell(2).getCellType() == Cell.CELL_TYPE_STRING) {
					String cant = NumberToTextConverter.toText(row.getCell(2).getNumericCellValue());
					Float cantidad = Float.parseFloat(cant);
					ingrediente.setCantidad(cantidad);
				}
				ingredienteRepo.save(ingrediente);
			} catch (Exception e) {
				System.out.println("Oh no!, algo ha ocurrido al momento de cargar los datos!");
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

}
