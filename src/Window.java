import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class Window {

    private static JFrame frame = new JFrame("Lightshot Navigator");
    private static JButton grab = new JButton("Grab");
    private static JButton save = new JButton("Save");
    private static JButton next = new JButton("Next");
    private static JButton previous = new JButton("Previous");
    private static boolean isImageOn = false;
    private boolean isLoading = false;
    private JTextField input = new JTextField("URL goes here...", 30);
    private JLabel label = new JLabel();
    private ArrayList<ImageIcon> imageArray = new ArrayList<>();
//    private LinkedList<ImageIcon> imageArray = new LinkedList<>();
    private int currentIndex = 0;

    public Window() {
        File savedImagesDir = new File("saved");
        if (!savedImagesDir.exists()) {
            savedImagesDir.mkdir();
        }
        frame.setLayout(new FlowLayout());
        frame.setSize(450, 160);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setFocusable(true);
        frame.requestFocusInWindow();
        frame.setVisible(true);
        frame.add(previous);
        frame.add(next);
        frame.add(input);
        frame.add(grab);
        frame.add(save);
        grab.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Thread a = new Thread() {
                    @Override
                    public void run() {
                        if (!isImageOn) {
                            if (input.getText().contains("prntscr")) {
                                input.setText("https://prnt.sc/" + input.getText().substring(19));
                            }
                            addImageIcon(input.getText());
                            addImages(input.getText());
                            frame.requestFocus();
                        } else {
                            // todo: something to replace this using threads or something lol
                            updateImageIcon(input.getText());
                            frame.requestFocus();
                        }
                    }
                };
                a.start();
            }
        });
        previous.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentIndex != 0 && isImageOn) {
                    input.setText("https://prnt.sc/" + LinkManager.decrementID(input.getText().substring(16, input.getText().length()), 1));
                    currentIndex--;
                    updateImageIcon(imageArray.get(currentIndex));
                    Thread a = new Thread() {
                        @Override
                        public void run() {
                            addToBeginning(input.getText());
                        }
                    };
                    if (!isLoading && currentIndex == 0) {
                        a.start();
                    }
                    System.out.println("Current index: " + currentIndex + "/" + (imageArray.size() - 1));
                    frame.requestFocus();
                }
            }
        });
        next.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentIndex != imageArray.size() - 1 && isImageOn) {
                    input.setText("https://prnt.sc/" + LinkManager.incrementID(input.getText().substring(16, input.getText().length()), 1));
                    currentIndex++;
                    updateImageIcon(imageArray.get(currentIndex));
                    Thread a = new Thread() {
                        @Override
                        public void run() {
                            addToEnd(input.getText());
                        }
                    };
                    if (!isLoading && currentIndex == imageArray.size() - 1) { // starts thread that adds more images to the array
                        a.start();
                    }
                    System.out.println("Current index: " + currentIndex + "/" + (imageArray.size() - 1));
                    frame.requestFocus();
                }
            }
        });
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isImageOn) {
                    ImageIcon imageIcon = (ImageIcon)label.getIcon();
                    Image image = imageIcon.getImage();
                    BufferedImage bImage = convertToBufferedImage(image);
                    String fileName =  input.getText().substring(16) + ".png";
                    File outputFile = new File(System.getProperty("user.dir") + "\\saved\\" + fileName);
                    try {
                        ImageIO.write(bImage, "png", outputFile);
                    } catch (IOException ie) {
                        ie.printStackTrace();
                    }
                    System.out.println("Saved as " + fileName);
                }
            }
        });

        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    if (currentIndex != 0 && isImageOn) {
                        input.setText("https://prnt.sc/" + LinkManager.decrementID(input.getText().substring(16, input.getText().length()), 1));
                        currentIndex--;
                        updateImageIcon(imageArray.get(currentIndex));
                        Thread a = new Thread() {
                            @Override
                            public void run() {
                                addToBeginning(input.getText());
                            }
                        };
                        if (!isLoading && currentIndex == 0) {
                            a.start();
                        }
                        System.out.println("Current index: " + currentIndex + "/" + (imageArray.size() - 1));
                        frame.requestFocus();
                    }
                }
                else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    if (currentIndex != imageArray.size() - 1 && isImageOn) {
                        input.setText("https://prnt.sc/" + LinkManager.incrementID(input.getText().substring(16, input.getText().length()), 1));
                        currentIndex++;
                        updateImageIcon(imageArray.get(currentIndex));
                        Thread a = new Thread() {
                            @Override
                            public void run() {
                                addToEnd(input.getText());
                            }
                        };
                        if (!isLoading && currentIndex == imageArray.size() - 1) { // starts thread that adds more images to the array
                            a.start();
                        }
                        System.out.println("Current index: " + currentIndex + "/" + (imageArray.size() - 1));
                        frame.requestFocus();
                    }
                }
                //Removes the current image in case a no-no picture appears
                else if (e.getKeyCode() == KeyEvent.VK_ALT) {
                    label.setIcon(null);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        input.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    Thread a = new Thread() {
                        @Override
                        public void run() {
                            if (isImageOn == false) {
                                addImageIcon(input.getText());
                                addImages(input.getText());
                                frame.requestFocus();
                            } else {
                                updateImageIcon(input.getText());
                                frame.requestFocus();
                            }
                        }
                    };
                    a.start();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
    }

    private void addImages(String url) {
        isLoading = true;
        try {
            imageArray.add(new ImageIcon(ImageIO.read(LinkManager.getURL(url))));
            System.out.println("Added " + url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        url = url.substring(16);
        System.out.println(url);
        url = LinkManager.decrementID(url, 5);
        for (int i = 0; i < 5; i++) { //adds images to the beginning of the array list
            try {
                URL direct = LinkManager.getURL("https://prnt.sc/" + url);
                imageArray.add(0, new ImageIcon(ImageIO.read(direct)));
                System.out.println("Added " + url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            url = LinkManager.incrementID(url, 1);
            currentIndex++;
        }

        url = LinkManager.incrementID(url, 1);

        for (int i = 0; i < 5; i++) { //adds images to the end of the array list
            try {
                URL direct = LinkManager.getURL("https://prnt.sc/" + url);
                imageArray.add(new ImageIcon(ImageIO.read(direct)));
                System.out.println("Added " + url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            url = LinkManager.incrementID(url, 1);
        }
        isLoading = false;
    }

    private void addToEnd(String url) {
        isLoading = true;
        url = url.substring(16);
        url = LinkManager.incrementID(url, 1);
        for (int i = 0; i < 10; i++) {
            try {
                URL direct = LinkManager.getURL("https://prnt.sc/" + url);
                System.out.println(url + " -> " + direct);
                System.out.println("Adding to the array");
                imageArray.add(new ImageIcon(ImageIO.read(direct)));
                System.out.println("Added");
            } catch (IOException e) {
                e.printStackTrace();
            }
            url = LinkManager.incrementID(url, 1);
        }
        isLoading = false;
    }

    private void addToBeginning(String url) {
        isLoading = true;
        url = url.substring(16);
        url = LinkManager.decrementID(url, 1);
        for (int i = 0; i < 10; i++) {
            try {
                URL direct = LinkManager.getURL("https://prnt.sc/" + url);
                System.out.println(url + "->" + direct);
                System.out.println("Adding to the array");
                imageArray.add(0, new ImageIcon(ImageIO.read(direct)));
                System.out.println("Added");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Looks like " + url + " is bugged.");
            }
            url = LinkManager.decrementID(url, 1);
            currentIndex++;
        }
        isLoading = false;
    }

    private void addImageIcon(String url) {
        try {
            URL firstDirect = LinkManager.getURL(url);
            label.setIcon(new ImageIcon(ImageIO.read(firstDirect)));
            frame.add(label);
            frame.pack();

            isImageOn = true;
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    // deprecated
    private void updateImageIcon(String url) {
        try {
            URL direct = LinkManager.getURL(url);
            label.setIcon(new ImageIcon(ImageIO.read(direct)));
            frame.pack();
        } catch (Exception ie) {
            ie.printStackTrace();
        }
    }

    private void updateImageIcon(ImageIcon icon) {
        label.setIcon(icon);
        frame.pack();
    }

    private BufferedImage convertToBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }
        //Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_4BYTE_ABGR);
        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        return bimage;
    }
}