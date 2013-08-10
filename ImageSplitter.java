

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.commons.io.FilenameUtils;

import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.PNGEncodeParam; //@Todo more encoders and user choice
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codec.TIFFDecodeParam; //@Todo more docedrs and user choice
import com.sun.media.jai.codecimpl.TIFFImage;

public class ImageSplitter extends JPanel implements ActionListener {

	private static final long serialVersionUID = -2685655382409831974L;

	JFileChooser chooser;
	String choosertitle;
	JButton go;
	JFrame frame;

	public static void main(String[] foo) {
		new ImageSplitter();
	}

	private void marchThroughImages(List<String> fileName, String directoryName, List<String> o_names)
			throws IOException {

		Raster raster[] = new Raster[fileName.size()];
		String message = "";
		WritableRaster wRaster_lower8 = null;
		WritableRaster wRaster_upper8 = null;
		ColorModel cm = null;
		
		//byte[] map = new byte[] {(byte)0x00, (byte)0xff};
	   // ColorModel cm = new IndexColorModel(1, 2, map, map, map);
	    
		final BufferedImage png_i = ImageIO.read(new File(directoryName + "\\cm.png"));
		cm = png_i.getColorModel();
			
		//wRaster = image.getData().createCompatibleWritableRaster();
		wRaster_lower8 = png_i.getData().createCompatibleWritableRaster(); //@TODO don't read it from file (build in program and user choice)
		wRaster_upper8 = png_i.getData().createCompatibleWritableRaster(); //@TODO don't read it from file (build in program and user choice)
	    
		int totalFiles = fileName.size();

		for (int i = 0; i < totalFiles; i++) {

			
				
	
			
			
			
			File file = new File(fileName.get(i));
			SeekableStream s = new FileSeekableStream(file);

			TIFFDecodeParam param = null;

			ImageDecoder dec = ImageCodec.createImageDecoder("tiff", s, param);

			raster[i] = dec.decodeAsRaster();

			TIFFImage image = (TIFFImage) dec.decodeAsRenderedImage();


			message += "Images Processed " + fileName.get(i) + " width: "
					+ raster[0].getWidth() + " height: "
					+ raster[0].getHeight() + " Pixel Size: "
					+ image.getColorModel().getPixelSize() + "\n";
			s.close();

		}
		
		PNGEncodeParam encParam = null;
		File file_o = null;
		FileOutputStream fileoutput = null;
		ImageEncoder enc = null;
		
		
		int w = raster[0].getWidth(), h = raster[0].getHeight();
		System.out.println("Oto kurwa : " + w + " " + h);
		int lower8Pixel[][][] = new int[w][h][3]; //change 3 to 4 for png with alpha
		int upper8Pixel[][][] = new int[w][h][3]; //change 3 to 4 for png with alpha
		for (int i = 0; i < totalFiles; i++) {
			for (int width = 0; width < w; width++) {
				for (int height = 0; height < h; height++) {
					int[] pixelA = null;

					pixelA = raster[i].getPixel(width, height, pixelA);

					if(width == 20 && height == 31) System.out.println("oto pixelA : " + Integer.toBinaryString(pixelA[0])); //for checking @TODO write 00.es from left to make 16 size
					lower8Pixel[width][height][0] = pixelA[0] % 256;
					lower8Pixel[width][height][1] = pixelA[1] % 256;
					lower8Pixel[width][height][2] = pixelA[2] % 256;
					//lower8Pixel[width][height][3] = 255; //if alpha
					
					if(width == 20 && height == 31) System.out.println("oto lowerA : " +  Integer.toBinaryString(lower8Pixel[width][height][0])); //for checking @TODO write 00.es from left to make 8 size
					
					
					upper8Pixel[width][height][0] = (pixelA[0] - lower8Pixel[width][height][0])/256;
					upper8Pixel[width][height][1] = (pixelA[1] - lower8Pixel[width][height][1])/256;
					upper8Pixel[width][height][2] = (pixelA[2] - lower8Pixel[width][height][2])/256;
					//upper8Pixel[width][height][3] = 255; //if alpha
					
					if(width == 20 && height == 31) System.out.println("oto upperA : " +  Integer.toBinaryString(upper8Pixel[width][height][0])); //for checking @TODO write 00.es from left to make 8 size
					
					
					wRaster_lower8.setPixel(width, height,
							lower8Pixel[width][height]);
					
					wRaster_upper8.setPixel(width, height,
							upper8Pixel[width][height]);

				}
			}
			
			file_o = new File(directoryName + "\\lower_"
					+ o_names.get(i) + ".png");
			fileoutput = new FileOutputStream(file_o);

			encParam = null;

			enc = ImageCodec.createImageEncoder("png", fileoutput,
					encParam);
			enc.encode(wRaster_lower8, cm);

			fileoutput.close();
			
			file_o = new File(directoryName + "\\upper_"
					+ o_names.get(i) + ".png");
			fileoutput = new FileOutputStream(file_o);

			encParam = null;

			enc = ImageCodec.createImageEncoder("png", fileoutput,
					encParam);
			enc.encode(wRaster_upper8, cm);

			fileoutput.close();

			
		}

		
		System.out.println(message);
	}

	public ImageSplitter() {

		frame = new JFrame("TIFF Image Calibrator");
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		frame.getContentPane().add(this, "Center");
		frame.setSize(this.getPreferredSize());
		frame.setVisible(true);
		go = new JButton("Select Folder");
		go.addActionListener(this);
		add(go);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		go.setEnabled(false);

		chooser = new JFileChooser();
		chooser.setCurrentDirectory(new java.io.File(
				"."));
		chooser.setDialogTitle(choosertitle);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		String directoryName = "";
		//
		// disable the "All files" option.
		//
		chooser.setAcceptAllFileFilterUsed(false);

		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {

			List<String> results = new ArrayList<String>();
			List<String> onlynames = new ArrayList<String>();
			File[] files = null;
			try {
				directoryName = chooser.getSelectedFile().getCanonicalPath();
				files = new File(directoryName).listFiles(new FilenameFilter() {

					@Override
					public boolean accept(File dir, String name) {
						if (name.toLowerCase().endsWith("tif")
								|| name.toLowerCase().endsWith("tiff"))
							return true;
						else
							return false;
					}
				});

				for (File file : files) {
					if (file.isFile()) {
						System.out.println(file.getCanonicalPath());
						results.add(file.getCanonicalPath());
						onlynames.add(FilenameUtils.removeExtension(file.getName()));
						//onlynames.add(FilenameUtils.getBaseName(file)); //not a string
					}
				}

				marchThroughImages(results, directoryName, onlynames);

			} catch (IOException e1) {
				e1.printStackTrace();
			}

		} else {
			System.out
					.println("No Selection.Please Select the Folder containing TIFF Images.");
		}

		go.setEnabled(true);
	}

	public Dimension getPreferredSize() {
		return new Dimension(200, 200);
	}

}