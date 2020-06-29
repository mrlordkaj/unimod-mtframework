/*
 * Copyright (C) 2020 Thinh
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.openitvn.format.msg;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.text.BadLocationException;

/**
 *
 * @author Thinh
 */
public class FontInspect extends javax.swing.JFrame {

    private final File file = new File("C:/Users/Thinh/Documents/font00_j_vn.png");
    private final File outFile = new File("C:/Users/Thinh/Documents/font00_j_mod.png");
    private final Dimension imageSize = new Dimension(2304, 1280);
    
    private final int cellSize = 36;
    private float scaleFactor;
    private final float imageRatio;
    private final BufferedImage fontImage;
    
    private final Rectangle rect = new Rectangle(0, 0, 36, 36);
    
    public FontInspect() throws IOException {
        initComponents();
        fontImage = ImageIO.read(new FileInputStream(file));
        imageRatio = fontImage.getWidth() / (float)fontImage.getHeight();
        int id = 2106;
        id = copyCell(id, 52, 3);   // Y
        id = copyCell(id, 78, 3);   // y
        id = copyCell(id, 28, 5);   // A
        id = copyCell(id, 184, 6);  // AW
        id = copyCell(id, 88, 6);   // AA
        id = copyCell(id, 185, 1);  // DD
        id = copyCell(id, 32, 5);   // E
        id = copyCell(id, 93, 6);   // EE
        id = copyCell(id, 36, 5);   // I
        id = copyCell(id, 42, 5);   // O
        id = copyCell(id, 102, 6);  // OO
        id = copyCell(id, 173, 6);  // OW
        id = copyCell(id, 48, 5);   // U
        id = copyCell(id, 181, 6);  // UW
        id = copyCell(id, 52, 2);   // Y
        id = copyCell(id, 54, 5);   // a
        id = copyCell(id, 211, 6);  // aw
        id = copyCell(id, 110, 6);  // aa
        id = copyCell(id, 309, 1);  // d
        id = copyCell(id, 58, 5);   // e
        id = copyCell(id, 115, 6);  // ee
        id = copyCell(id, 62, 5);   // i
        id = copyCell(id, 68, 5);   // o
        id = copyCell(id, 124, 6);  // oo
        id = copyCell(id, 265, 6);  // ow
        id = copyCell(id, 74, 5);   // u
        id = copyCell(id, 224, 6);  // uw
        id = copyCell(id, 78, 2);   // y
//        System.out.println(id);
//        ImageIO.write(fontImage, "png", outFile);
    }
    
    private int copyCell(int dstId, int srcId, int iteration) {
        int srcRow = srcId / 64;
        int srcCol = srcId % 64;
        int[] clearData = new int[36*36];
        BufferedImage cellImg = fontImage.getSubimage(srcCol * cellSize, srcRow * cellSize, cellSize, cellSize);
        for (int i = 0; i < iteration; i++) {
            int row = dstId / 64;
            int col = dstId % 64;
            int x = col * cellSize;
            int y = row * cellSize;
            fontImage.setRGB(x, y, cellSize, cellSize, clearData, 0, cellSize);
            fontImage.getGraphics().drawImage(cellImg, x, y, null);
            dstId++;
        }
        return dstId;
    }
    
    private void paintViewport(Graphics2D g) {
        Dimension scaleSize = imagePanel.getSize();
        g.setBackground(Color.BLACK);
        g.clearRect(0, 0, scaleSize.width, scaleSize.height);
        float scaleRatio = scaleSize.width / (float)scaleSize.height;
        if (scaleRatio < imageRatio) {
            // preserve width
            scaleFactor = scaleSize.width / (float)fontImage.getWidth();
            scaleSize.height = (int)(fontImage.getHeight() * scaleFactor);
        } else {
            // preserve height
            scaleFactor = scaleSize.height / (float)fontImage.getHeight();
            scaleSize.width = (int)(fontImage.getWidth()* scaleFactor);
        }
        g.drawImage(fontImage.getScaledInstance(scaleSize.width, scaleSize.height, Image.SCALE_SMOOTH), 0, 0, null);
        g.setColor(Color.RED);
//        g.setStroke(new BasicStroke(2));
        int x = (int)(rect.x * scaleFactor);
        int y = (int)(rect.y * scaleFactor);
        int width = (int)(rect.width * scaleFactor);
        int height = (int)(rect.height * scaleFactor);
        g.drawRect(x, y, width, height);
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        imagePanel = new javax.swing.JPanel() {
            public void paint(Graphics g) {
                paintViewport((Graphics2D)g);
            }
        };
        txtCode = new javax.swing.JTextField();
        btnFindPosition = new javax.swing.JButton();
        lblId = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });

        imagePanel.setBackground(new java.awt.Color(0, 0, 0));
        imagePanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                imagePanelMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout imagePanelLayout = new javax.swing.GroupLayout(imagePanel);
        imagePanel.setLayout(imagePanelLayout);
        imagePanelLayout.setHorizontalGroup(
            imagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        imagePanelLayout.setVerticalGroup(
            imagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 657, Short.MAX_VALUE)
        );

        txtCode.setText("00620084");

        btnFindPosition.setText("Find Position");
        btnFindPosition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFindPositionActionPerformed(evt);
            }
        });

        lblId.setText("<id>");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(imagePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblId)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 749, Short.MAX_VALUE)
                .addComponent(txtCode, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnFindPosition)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(imagePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFindPosition)
                    .addComponent(lblId))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnFindPosition, lblId, txtCode});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        imagePanel.repaint();
    }//GEN-LAST:event_formComponentResized

    private void btnFindPositionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFindPositionActionPerformed
        try {
            int widthData = Short.parseShort(txtCode.getText(0, 4), 16);
            int indexData = Short.parseShort(txtCode.getText(4, 4), 16) >> 1;
            int row = indexData / 64;
            int col = indexData % 64;
            rect.x = col * cellSize;
            rect.y = row * cellSize;
//            rect.width = widthData / 2;
            imagePanel.repaint();
            System.out.println(widthData);
        } catch (BadLocationException ex) {
            ex.printStackTrace(System.err);
        }
    }//GEN-LAST:event_btnFindPositionActionPerformed

    private void imagePanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_imagePanelMouseClicked
        int x = (int)(evt.getX() / scaleFactor / cellSize);
        int y = (int)(evt.getY() / scaleFactor / cellSize);
        int id = y * 64 + x;
        lblId.setText(Integer.toString(id));
    }//GEN-LAST:event_imagePanelMouseClicked
    
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new FontInspect().setVisible(true);
                } catch (IOException ex) { }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFindPosition;
    private javax.swing.JPanel imagePanel;
    private javax.swing.JLabel lblId;
    private javax.swing.JTextField txtCode;
    // End of variables declaration//GEN-END:variables
}
