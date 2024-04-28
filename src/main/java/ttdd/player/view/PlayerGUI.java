package ttdd.player.view;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.mpatric.mp3agic.*;

public class PlayerGUI extends JFrame {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1L;
	private static String urlPath = "";
	private static String urlParentFromFiles = "";
	private static AdvancedPlayer player;
	private static FileInputStream fis;
	private static volatile boolean isPlaying = false;
	private static final int RECTANGLE_WIDTH = 400; 
	private Integer thread_x = 0;
	private static List <String> mp3Files = new ArrayList<>();
	private static List <String> durationMp3 = new ArrayList<>();
	private static File[] files;
	private static File directory;
	private static DefaultTableModel tableModel;
	private static JTable table;
	private static JScrollPane scrollPane;
	private static JPanel panel;
	public void setMusicPlay() throws JavaLayerException {

		try {
	        fis = new FileInputStream(urlPath);
	        PlayerGUI.player = new AdvancedPlayer(fis);
	        
	        isPlaying = true; // Indica que la reproducción está en curso
	        player.play(); // Inicia la reproducción del archivo de audio
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    } finally {
	        isPlaying = false; // Marca la reproducción como finalizada
	        if (player != null) {
	            player.close(); // Cierra el reproductor cuando la reproducción se detiene
	        }
	    }
	}

	public void setMusicStop() {
		isPlaying = false; // Detiene la reproducción al cambiar la bandera
	    if (player != null) {
	        player.close(); // Cierra el reproductor si está reproduciendo
	    }
	    if (fis != null) {
	        try {
	            fis.close(); // Cierra el flujo de entrada del archivo
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    thread_x = 0;
	}

	public void setTrackToPlay(String pathTo){
		PlayerGUI.urlPath = pathTo;
	}

    // Método para escanear un directorio y agregar los archivos mp3 a la lista
    private void scanDirectory(String directoryPath) {
    	PlayerGUI.directory = new File(directoryPath);
        PlayerGUI.files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().toLowerCase().endsWith(".mp3")) {
                    mp3Files.add(file.getName());
                }
            }
        }
    }
    
    private String setLength(String pathTo) {
    	long durationInSeconds = 0, minutes = 0, seconds = 0;
    	
    	try {
            Mp3File mp3File = new Mp3File(pathTo); // Reemplaza con la ruta de tu archivo MP3
            durationInSeconds = mp3File.getLengthInSeconds();
            minutes = durationInSeconds / 60;
            seconds = durationInSeconds % 60;
            System.out.println("Duración del archivo MP3: " + minutes + " minutos " + seconds + " segundos");
        } catch (IOException | UnsupportedTagException | InvalidDataException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    	return minutes + ":" + seconds;
    }
    
	// Método para actualizar el modelo de datos de la tabla con los nombres de los
	// archivos
	private void updateTable() throws UnsupportedAudioFileException, IOException {
		System.out.println("Cantidad de elementos: " + PlayerGUI.tableModel.getRowCount());

		if (PlayerGUI.mp3Files.size() > 0) {
			
			for (String fileName : PlayerGUI.mp3Files) {
				
				//PlayerGUI.tableModel.setRowCount(0);
				String filePath = PlayerGUI.urlParentFromFiles + "\\" + fileName;
				System.out.println("Ruta: " + filePath);
				PlayerGUI.durationMp3.add(setLength(filePath));
				
			}

		}
		
		if (PlayerGUI.tableModel.getRowCount() == 0) {
			// PlayerGUI.tableModel.addRow(new Object[] {
			// PlayerGUI.mp3Files.get(0).toString() });
			int i = 0;
			for (String fileName : PlayerGUI.mp3Files) {
				PlayerGUI.tableModel.addRow(new Object[] { fileName,PlayerGUI.durationMp3.get(i) }); // Agregar cada nombre de archivo como una fila													// en la tabla
				i++;
			}
		} else {
			PlayerGUI.tableModel.setRowCount(0);
			// Limpiar la tabla
			int i = 0;
			for (String fileName : PlayerGUI.mp3Files) {
				PlayerGUI.tableModel.addRow(new Object[] { fileName,PlayerGUI.durationMp3.get(i) }); // Agregar cada nombre de archivo como una fila													// en la tabla
				i++;
			}
		}
		
	}
	
	public PlayerGUI() {
		
	        setTitle("Reproductor de Música");
	        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        setSize(800, 600);
	        setLocationRelativeTo(null); // Centra la ventana en la pantalla
	        
	        
	        /*
	        JPanel panel = new JPanel() {
	            @Override
	            protected void paintComponent(Graphics g) {
	                super.paintComponent(g);
	                g.setColor(Color.BLACK); // Establece el color del trazo en negro
	                g.drawRect((getWidth() - RECTANGLE_WIDTH) / 2, 50, RECTANGLE_WIDTH, 100); // Dibuja el borde exterior del rectángulo en negro

	                g.setColor(Color.WHITE); // Establece el color de relleno en blanco
	                g.fillRect((getWidth() - RECTANGLE_WIDTH) / 2 + 1, 51, RECTANGLE_WIDTH - 1, 99); // Rellena el interior del rectángulo con blanco

	                g.setColor(Color.BLACK); // Restablece el color del trazo en negro
	                g.drawRect((getWidth() - RECTANGLE_WIDTH) / 2 + 1, 51, RECTANGLE_WIDTH - 2, 98); // Dibuja el borde interior del rectángulo en negro
	              
	            }
	        };
			*/
	       
	        JButton playButton = new JButton("Play");
	        JButton stopButton = new JButton("Stop");
	      	
	        playButton.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	            	
	                // Coloca aquí la lógica para reproducir la música
	            	if("".equalsIgnoreCase(urlPath)) {
	            		System.out.println("Debe cargar un tema.");
	            	}
	            	if(thread_x == 0 && !"".equalsIgnoreCase(urlPath)) {
	            		System.out.println("Reproduciendo...");
	            	}
	            	if(thread_x == 1) {
	            		System.out.println("Ya existe un tema en curso.");
	            	}
	              
	                
	                
	                if(thread_x == 0 && !"".equalsIgnoreCase(urlPath)) {
	                    try {
		                    // Crea un hilo nuevo para reproducir la música
		                    Thread playerThread = new Thread(new Runnable() {
		                        public void run() {
		                            try {
		                                isPlaying = true; // Inicia la reproducción
		                                setMusicPlay();
		                            } catch (JavaLayerException ex) {
		                                ex.printStackTrace();
		                            }finally {
		                            	System.out.println("El tema terminó.");
		                            	isPlaying = false; //Marca la reproducción como finalizada
		                            	thread_x = 0;// Marca la reproducción como finalizada
		                                if (player != null) {
		                                    setMusicStop(); // Cierra el reproductor cuando la reproducción se detiene
		                                }
		                            }
		                        }
		                    });
		                    playerThread.start();// Inicia el hilo para reproducir la música
		                    //playerThread.join();
		                    thread_x++;
		                } catch (Exception ex) {
		                    ex.printStackTrace();
		                }
					} else if (thread_x > 1) {
						setMusicStop();
					}
	            }
	        });

	        stopButton.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                // Coloca aquí la lógica para detener la música
	                System.out.println("Deteniendo...");
	                setMusicStop();
	            }
	        });

	        JPanel filePanel = new JPanel();
	        JButton openButton = new JButton("Abrir Archivo");
	        openButton.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                JFileChooser fileChooser = new JFileChooser();
	                fileChooser.setCurrentDirectory(new File(PlayerGUI.urlParentFromFiles));
	                FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos MP3", "mp3");
	                fileChooser.setFileFilter(filter);
	                int returnValue = fileChooser.showOpenDialog(null);
	                if (returnValue == JFileChooser.APPROVE_OPTION) {
	                	
	                    urlPath = fileChooser.getSelectedFile().getPath(); // Asignar la ruta seleccionada a urlPath
	                    
	                    String urlAbsolute = fileChooser.getSelectedFile().getParent();
	                    
	                    if("".equalsIgnoreCase(PlayerGUI.urlParentFromFiles)||
	                    		!PlayerGUI.urlParentFromFiles.equalsIgnoreCase(urlAbsolute)) {
	                    	PlayerGUI.urlParentFromFiles = urlAbsolute;
	                    	scanDirectory(PlayerGUI.urlParentFromFiles);
	                    	System.out.println("Archivos escaneados: "  + mp3Files.toString());	
	                    	try {
								updateTable();
							} catch (UnsupportedAudioFileException | IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
	                    }
	                    
	                    System.out.println("Archivo seleccionado: " + urlPath);
	                    System.out.println("Ruta del directorio: " + urlAbsolute);
	                }
	                
	            }
	        });
	        PlayerGUI.panel = new JPanel();
	        panel.setBackground(Color.WHITE);
	        // Crear modelo de datos para la tabla
	        
	        PlayerGUI.tableModel = new DefaultTableModel(){
	            @Override
	            public boolean isCellEditable(int row, int column) {
	                return false; // Hacer todas las celdas no editables
	            }
	        };
	        
	        tableModel.addColumn("Archivo mp3");
	        tableModel.addColumn("Duracion");
	       	
	        	
	        // Crear tabla con el modelo de datos
	        //JTableHeader tableHeader = new JTableHeader();
	     
	        PlayerGUI.table = new JTable(tableModel);
	     // Crear renderizador de celdas para las columnas adicionales
	        /*
	        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
	        renderer.setHorizontalAlignment(JLabel.CENTER); // Alineación del texto en el centro
			*/
	        
	        // Configurar la columna adicional 1
	        //table.getColumnModel().addColumn(new TableColumn());
	        //table.getColumnModel().getColumn(0).setHeaderValue("Pista");
	        //table.getColumnModel().getColumn(0).setCellRenderer(renderer);
	        
	        /*
	        // Configurar la columna adicional 2
	        table.getColumnModel().addColumn(new TableColumn());
	        table.getColumnModel().getColumn(1).setHeaderValue("Duración");
	        table.getColumnModel().getColumn(1).setCellRenderer(renderer);
	        */
	       
	        // Agregar tabla a un JScrollPane para permitir el desplazamiento si hay muchos archivos
	        PlayerGUI.scrollPane = new JScrollPane(table);

	        
	        panel.add(playButton);
	        panel.add(stopButton);
	        panel.add(filePanel, BorderLayout.NORTH);
	        panel.add(scrollPane,BorderLayout.CENTER);
	        filePanel.add(openButton);
	        		
	        add(panel);
	        
	        //debemos hacer sensible el tema
	     // Añadir ListSelectionListener al JTable
	        PlayerGUI.table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
	            @Override
	            public void valueChanged(ListSelectionEvent e) {
	                if (!e.getValueIsAdjusting()) { // Verificar si la selección ha finalizado
	                    int selectedRow = table.getSelectedRow(); // Obtener la fila seleccionada

	                    if (selectedRow != -1) { // Verificar si se ha seleccionado alguna fila
	                        // Obtener el nombre del tema de la fila seleccionada
	                        String nombreTema = PlayerGUI.urlParentFromFiles + "\\" +
	                        tableModel.getValueAt(selectedRow, 0).toString(); // Suponiendo que la primera columna contiene los nombres de los temas

	                        // Detener la reproducción actual si está en curso
	                        setMusicStop();
	                        System.out.println("Ruta: " + nombreTema);
	                        // Establecer el nuevo tema a reproducir
	                        setTrackToPlay(nombreTema);
	                        // Reproducir el nuevo tema
	                        playButton.doClick();
	                      
	                    }
	                }
	            }
	        });

	    }
}
