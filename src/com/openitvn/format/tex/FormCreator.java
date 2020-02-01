/*
 * Copyright (C) 2017 Thinh Pham
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
package com.openitvn.format.tex;

import com.openitvn.unicore.Unicore;
import com.openitvn.unicore.raster.IPixelFormat;
import com.openitvn.util.FormHelper;

/**
 *
 * @author Thinh Pham
 */
class FormCreator extends javax.swing.JDialog {
    
    protected MTTextureVersion version;
    protected MTTextureVariant variant;
    protected short width;
    protected short height;
    protected byte faceCount;
    protected byte mipCount;
    protected IPixelFormat format;
    protected boolean isCancelled = true;
    
    protected FormCreator(MTTextureVersion ver, MTTextureVariant var, short width, short height, int faceCount, int mipCount, IPixelFormat fmt) throws UnsupportedOperationException {
        super(Unicore.getMainFrame(), true);
        initComponents();
        //create version list box
        for (MTTextureVersion curVer : MTTextureVersion.values())
            cboVersion.addItem(curVer);
        if (ver != null)
            cboVersion.setSelectedItem(ver);
        //create variant list box
        for (MTTextureVariant curVar : MTTextureVariant.values())
            cboVariant.addItem(curVar);
        if (var != null)
            cboVariant.setSelectedItem(var);
        if (faceCount != 6)
            cboVariant.removeItem(MTTextureVariant.CubeMap);
        //fill width, height text boxes
        this.width = width;
        txtWidth.setText(Integer.toString(width));
        this.height = height;
        txtHeight.setText(Integer.toString(height));
        //set face count
        txtFace.setText(Integer.toString(faceCount));
        //set mipmap
        chkMipMap.setSelected(mipCount > 1);
        chkMipMap.setText(Integer.toString(mipCount));
        //create encode list
        cboEncode.addItem(IPixelFormat.D3DFMT_A8R8G8B8);
        switch (fmt) {
            case D3DFMT_DXT1:
            case DXGI_FORMAT_BC1_UNORM:
                cboEncode.addItem(IPixelFormat.D3DFMT_DXT1);
                cboEncode.setSelectedItem(IPixelFormat.D3DFMT_DXT1);
                break;
                
            case D3DFMT_DXT3:
            case DXGI_FORMAT_BC2_UNORM:
                cboEncode.addItem(IPixelFormat.D3DFMT_DXT3);
                cboEncode.setSelectedItem(IPixelFormat.D3DFMT_DXT3);
                break;
                
            case D3DFMT_DXT5:
            case DXGI_FORMAT_BC3_UNORM:
                cboEncode.addItem(IPixelFormat.D3DFMT_DXT5);
                cboEncode.setSelectedItem(IPixelFormat.D3DFMT_DXT5);
                break;
                
            case D3DFMT_A8R8G8B8:
            case DXGI_FORMAT_B8G8R8A8_UNORM:
                //cboEncode.addItem(IPixelFormat.D3DFMT_A8R8G8B8);
                cboEncode.setSelectedItem(IPixelFormat.D3DFMT_A8R8G8B8);
                break;
                
            default:
                throw new UnsupportedOperationException("Source's format is not compatible with MT Texture.");
        }
        FormHelper.setToCenter(FormCreator.this, Unicore.getMainFrame());
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cboVersion = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        cboVariant = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        btnProcess = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        txtWidth = new javax.swing.JTextField();
        txtHeight = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        cboEncode = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        chkMipMap = new javax.swing.JCheckBox();
        jLabel7 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtFace = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("MT Texture Creator");
        setResizable(false);
        setType(java.awt.Window.Type.POPUP);

        cboVersion.setFocusable(false);

        jLabel1.setText("Version:");

        cboVariant.setFocusable(false);

        jLabel2.setText("Variant:");

        btnProcess.setText("Process");
        btnProcess.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProcessActionPerformed(evt);
            }
        });

        jLabel3.setText("Width:");

        txtWidth.setEnabled(false);
        txtWidth.setFocusable(false);
        txtWidth.setPreferredSize(new java.awt.Dimension(80, 20));

        txtHeight.setEnabled(false);
        txtHeight.setFocusable(false);

        jLabel5.setText("Height:");

        cboEncode.setFocusable(false);

        jLabel4.setText("Format:");

        chkMipMap.setText("1");
        chkMipMap.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        chkMipMap.setEnabled(false);
        chkMipMap.setFocusable(false);

        jLabel7.setText("MipMap:");

        jLabel6.setText("Face:");

        txtFace.setEnabled(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnProcess))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel5)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cboEncode, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtHeight, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtWidth, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 256, Short.MAX_VALUE)
                            .addComponent(cboVariant, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cboVersion, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkMipMap)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFace)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2, jLabel3, jLabel4, jLabel5, jLabel6, jLabel7});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboVersion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboVariant, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtWidth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtHeight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtFace, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(chkMipMap))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboEncode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnProcess)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnProcessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProcessActionPerformed
        version = (MTTextureVersion)cboVersion.getSelectedItem();
        variant = (MTTextureVariant)cboVariant.getSelectedItem();
        width = Short.parseShort(txtWidth.getText());
        height = Short.parseShort(txtHeight.getText());
        faceCount = Byte.parseByte(txtFace.getText());
        mipCount = Byte.parseByte(chkMipMap.getText());
        format = (IPixelFormat)cboEncode.getSelectedItem();
        isCancelled = false;
        dispose();
    }//GEN-LAST:event_btnProcessActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnProcess;
    private javax.swing.JComboBox<IPixelFormat> cboEncode;
    private javax.swing.JComboBox<MTTextureVariant> cboVariant;
    private javax.swing.JComboBox<MTTextureVersion> cboVersion;
    private javax.swing.JCheckBox chkMipMap;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JTextField txtFace;
    private javax.swing.JTextField txtHeight;
    private javax.swing.JTextField txtWidth;
    // End of variables declaration//GEN-END:variables
}
