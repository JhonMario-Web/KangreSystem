package com.KangreSystem.models.imp;

import java.util.ArrayList;
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
import com.KangreSystem.models.entity.CategoriaInsumo;
import com.KangreSystem.models.entity.Insumo;
import com.KangreSystem.models.entity.Proveedor;
import com.KangreSystem.models.repository.CategoriaInsumoRepository;
import com.KangreSystem.models.repository.InsumoRepository;
import com.KangreSystem.models.repository.ProveedorRepository;
import com.KangreSystem.models.service.IInsumoService;

@Service
public class InsumoServiceImp implements IInsumoService {
	
	@Autowired
	private InsumoRepository insumoRepository;
	
	@Autowired
	private CategoriaInsumoRepository categoriaRepository;
	
	@Autowired
	private ProveedorRepository proveedorRepository;
	
	@Override
	public List<Insumo> listar() {
		return (List<Insumo>) insumoRepository.findAll();
	}

	@Override
	public void guardar(Insumo insumo) {
		insumo.setNombre(insumo.getNombre().toUpperCase());
		insumoRepository.save(insumo);
	}

	@Override
	public Insumo buscarPorId(Long idInsumo) {
		return insumoRepository.findById(idInsumo).orElse(null);
	}

	@Override
	public void eliminar(Long idInsumo) {
		insumoRepository.deleteById(idInsumo);
	}


	@Override
	public Insumo buscarPorNombre(String nombre) {
		return insumoRepository.findByNombre(nombre);
	}

	@Override
	public List<Insumo> buscarPorNombreList(String nombre) {
		return insumoRepository.findByNombreContaining(nombre);
	}

	@Override
	public List<Insumo> buscarPorCategoria(CategoriaInsumo categoria) {
		return insumoRepository.findByCategoria(categoria);
	}

	@Override
	public List<Insumo> buscarPorProveedor(Proveedor proveedor) {
		return insumoRepository.findByProveedor(proveedor);
	}

	@Override
	public List<Insumo> buscarPorEstado(String estado) {
		return insumoRepository.findByEstado(estado);
	}

	@Override
	public List<Insumo> buscarPorCategoriaProveedorEstado(CategoriaInsumo categoria, Proveedor proveedor, String estado) {
		if (categoria != null && proveedor == null && estado.isEmpty()) {
			return insumoRepository.findByCategoria(categoria);
		}
		if (categoria == null && proveedor != null && estado.isEmpty()) {
			return insumoRepository.findByProveedor(proveedor);
		}
		if (categoria == null && proveedor == null && !estado.isEmpty()) {
			return insumoRepository.findByEstado(estado);
		}
		if (categoria != null && proveedor != null && !estado.isEmpty()) {
			return insumoRepository.findByCategoriaAndProveedorAndEstado(categoria, proveedor, estado);
		}
		if (categoria != null && proveedor != null && estado.isEmpty()) {
			return insumoRepository.findByCategoriaAndProveedor(categoria, proveedor);
		}
		if (categoria != null && proveedor == null && !estado.isEmpty()) {
			return insumoRepository.findByCategoriaAndEstado(categoria, estado);
		}
		if (categoria == null && proveedor != null && !estado.isEmpty()) {
			return insumoRepository.findByProveedorAndEstado(proveedor, estado);
		}
		return (List<Insumo>) insumoRepository.findAll();
	}

	@Override
	public List<Insumo> buscarPorCategoriaProveedor(CategoriaInsumo categoria, Proveedor proveedor) {
		return insumoRepository.findByCategoriaAndProveedor(categoria, proveedor);
	}

	@Override
	public List<Insumo> buscarPorCategoriaEstado(CategoriaInsumo categoria, String estado) {
		return insumoRepository.findByCategoriaAndEstado(categoria, estado);
	}

	@Override
	public List<Insumo> buscarPorProveedorEstado(Proveedor proveedor, String estado) {
		return insumoRepository.findByProveedorAndEstado(proveedor, estado);
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
		CategoriaInsumo categoria = null;
		Proveedor proveedor = null;
		int stockMin = 0;
		int stockMax = 0;
		
		rows.next();
		
		while (rows.hasNext()) {
			
			try {
				Row row = rows.next();
				Insumo insumo = new Insumo();
				
				if (row.getCell(0).getCellType() == Cell.CELL_TYPE_STRING) {
					insumo.setNombre(row.getCell(0).getStringCellValue());
				
					if (insumoRepository.existsByNombre(row.getCell(0).getStringCellValue())) {
						return false;
					}
				}
				
				if (row.getCell(1).getCellType() == Cell.CELL_TYPE_NUMERIC) {
					String id = NumberToTextConverter.toText(row.getCell(1).getNumericCellValue());
					Long idCategoria = Long.parseLong(id);
					categoria = categoriaRepository.findById(idCategoria).orElse(null);
					
					if (categoria == null) {
						return false;
					}
					
					insumo.setCategoria(categoria);
				}	else if (row.getCell(1).getCellType() == Cell.CELL_TYPE_STRING) {
					insumo.setCategoria(categoria);
				}
				
				if (row.getCell(2).getCellType() == Cell.CELL_TYPE_NUMERIC) {
					String nitProveedor = NumberToTextConverter.toText(row.getCell(2).getNumericCellValue());
					
					proveedor = proveedorRepository.findByNitProveedor(nitProveedor);
					
					if (proveedor == null) {
						return false;
					}
					
					insumo.setProveedor(proveedor);
				}	else if (row.getCell(2).getCellType() == Cell.CELL_TYPE_STRING) {
					insumo.setProveedor(proveedor);
				}
				
				if (row.getCell(3).getCellType() == Cell.CELL_TYPE_NUMERIC) {
					String price = NumberToTextConverter.toText(row.getCell(3).getNumericCellValue());
					Long precio = Long.parseLong(price);
					
					insumo.setPrecio(precio);
				}	else if (row.getCell(3).getCellType() == Cell.CELL_TYPE_STRING) {
					String price = NumberToTextConverter.toText(row.getCell(3).getNumericCellValue());
					Long precio = Long.parseLong(price);
					insumo.setPrecio(precio);
				}
				
				if (row.getCell(4).getCellType() == Cell.CELL_TYPE_STRING) {
					String dev = row.getCell(4).getStringCellValue();
					boolean devolucion = Boolean.parseBoolean(dev.toLowerCase());
					insumo.setDevolucion(devolucion);
				}
				
				if (row.getCell(5).getCellType() == Cell.CELL_TYPE_NUMERIC) {
					String min = NumberToTextConverter.toText(row.getCell(5).getNumericCellValue());
					stockMin = Integer.parseInt(min);
					
					insumo.setStockMin(stockMin);
				}	else if (row.getCell(5).getCellType() == Cell.CELL_TYPE_STRING) {
					String min = NumberToTextConverter.toText(row.getCell(5).getNumericCellValue());
					stockMin = Integer.parseInt(min);
					insumo.setStockMin(stockMin);
				}
				
				if (row.getCell(6).getCellType() == Cell.CELL_TYPE_NUMERIC) {
					String max = NumberToTextConverter.toText(row.getCell(6).getNumericCellValue());
					stockMax = Integer.parseInt(max);
					
					insumo.setStockMax(stockMax);
				}	else if (row.getCell(6).getCellType() == Cell.CELL_TYPE_STRING) {
					String max = NumberToTextConverter.toText(row.getCell(6).getNumericCellValue());
					stockMax = Integer.parseInt(max);
					insumo.setStockMax(stockMax);
				}
				
				if (stockMin > stockMax) {
					return false;
				}
				
				insumo.setStockActual((float) 0);
				insumo.setEntradas(0);
				insumo.setSalidas(0);
				insumo.setEstado("ACTIVO");
				insumo.setTipoArchivo(FilenameUtils.getExtension(file.getOriginalFilename()));
				insumoRepository.save(insumo);
			} catch (Exception e) {
				System.out.println("Oh no!, Algo ha ocurrido durante el ingreso de datos!");
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
	public Long totalInsumos() {
		return insumoRepository.count();
	}

	@Override
	public List<Insumo> buscarPorProveedorDevolucion(Proveedor proveedor, boolean devolucion) {
		List<Insumo> AllInsumos = insumoRepository.findByProveedorAndDevolucion(proveedor, devolucion);
		List<Insumo> insumos = new ArrayList<>();
		
		for (Insumo insumo : AllInsumos) {
			if (insumo.getStockActual() > 0) {
				insumos.add(insumo);
			}
		}
		
		return insumos;
	}

	@Override
	public List<Insumo> insumosConExistencia() {
		List<Insumo> insumos = (List<Insumo>) insumoRepository.findAll();
		List<Insumo> insumosConExistencia = new ArrayList<>();
		
		for (Insumo insumo : insumos) {
			if (insumo.getStockActual() > 0) {
				insumosConExistencia.add(insumo);
			}
		}
		
		return insumosConExistencia;
	}

}
