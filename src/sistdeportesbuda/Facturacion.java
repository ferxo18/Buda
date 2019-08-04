
package sistdeportesbuda;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import java.awt.*;
import java.awt.print.PageFormat;
import static java.awt.print.Printable.NO_SUCH_PAGE;
import static java.awt.print.Printable.PAGE_EXISTS;
import java.awt.print.PrinterException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;


public class Facturacion extends javax.swing.JFrame implements Printable {
    public static final String Url = "jdbc:mysql://localhost:3306/deportesbuda";
    public static final String Username = "root";
    public static final String Pasword = "";
    PreparedStatement ps;
    ResultSet rs;
    //private float Subtotal=0;
   
    
    public static Connection getConnection(){// Metodo para Coneccion de la base de Datos
        Connection con = null;
        try{ 
         Class.forName("com.mysql.jdbc.Driver");
         con = (Connection)DriverManager.getConnection(Url, Username, Pasword);
         //JOptionPane.showMessageDialog(null, "Coneccion Exitosa");
         System.out.print("Conexion Exitosa");
        
        }catch(Exception e){
         System.out.println("Error De Coneccion");
        }
                
        
        return con;
        
    }
    
    
   /*public static String FechaActual(){
       Date fecha = new Date();
       SimpleDateFormat formatofecha = new SimpleDateFormat ("YYYY-MM-dd");
       return formatofecha.format(fecha);
       
   }*/
    
    public void nfact(){
           
         Connection con = null;
          try{
         con = getConnection();
         String M=null;
        
        ps = con.prepareStatement("SELECT MAX(NumFact) FROM factura");// Instruccion para traer numero de Fcatura
           rs = ps.executeQuery();
           
           if(rs.next()){
            
            M = rs.getString("Max(NumFact)");
           
           }else{
               JOptionPane.showMessageDialog(null, "No Existe el Registro");
           }
            
          int max = Integer.parseInt(M)+1;
          String nf = Integer.toString(max);
          
          if(max>0&&max<=9){                                   // procedimiento para mostrar Numero de Factura
          txtNfact.setText("000"+nf);
          }
          else if(max>=10&&max<=99){
           txtNfact.setText("00"+nf);   
          } 
          else if(max>=100&&max<=999){
           txtNfact.setText("0"+nf);   
          }
           else if(max>=1000){
           txtNfact.setText(nf);   
          }
          
                  
          con.close();
         
        }catch(Exception e){
          System.out.println("Error al Mostrar Datos");
        }
    }
   DefaultTableModel dtm = new DefaultTableModel();
    
   public void agregar(){
       Connection con = null; 
       
       String cod = null, desc=null, prec = null, canti=null;
       
       int cf=0,ci=0;
       Float p,t;
       try{
         con = getConnection();
         
         
         ps = con.prepareStatement("SELECT * FROM inventario WHERE CodArticulo = ?");
         ps.setString(1, txtCodigo.getText());
          rs = ps.executeQuery();
           
           if(rs.next()){
               canti=rs.getString("Cantidad"); 
            
            
           }else{
               JOptionPane.showMessageDialog(null, "No Existe el Registro");
           }
            
           ci = Integer.parseInt(canti);// convierto cantidad de en invenratio a entero
            cf =Integer.parseInt(txtCantidad.getText());// Convierto cantidad a facturar a entero 
           
            if(ci>=cf){ 
              ps = con.prepareStatement("SELECT * FROM inventario WHERE CodArticulo = ?");
          ps.setString(1, txtCodigo.getText());
          rs = ps.executeQuery();
           
           if(rs.next()){
                
            cod=rs.getString("CodArticulo");
            desc=rs.getString("Descripcion");            
            prec =rs.getString("Precio");
            
           } 
           
           }else{
                JOptionPane.showMessageDialog(null, "No puedes Caer en Inventario Negativo");
                
                Limpiar1();
            }
           
           con.close();     
        
         }catch(Exception e){
        System.out.println("Error");
    }  
       ci=Integer.parseInt(txtCantidad.getText());//trnformo variables para presentar total en la tabla
       p=Float.parseFloat(prec);
       t=ci*p;
        int x = 0;
       for(int i=0;i<Tabla.getRowCount();i++){
      if(Tabla.getValueAt(i, 0).toString().equals(cod)){
          x=1;
      }
   }
       if(x==0){         
       dtm.addRow(new Object[]{          
            
     cod,desc,prec,ci,t                                   
   });
    
       }else{
           JOptionPane.showMessageDialog(null, "Registro ya Existe en la Tabla");
       }
       
                 
       float fila=0;
       float Subtotal=0;
       float itbms=0,total=0;
       
       for(int i=0;i<Tabla.getRowCount();i++){
          fila = Float.parseFloat(Tabla.getValueAt(i, 4).toString());
          Subtotal += fila;
             
       }
       itbms=(float) (Subtotal*0.07);
       total=Subtotal+itbms;
     DecimalFormatSymbols s = new DecimalFormatSymbols();
     s.setDecimalSeparator('.');
       DecimalFormat df = new DecimalFormat("0.00",s);     
       
       String st = df.format(Subtotal);
       String i = df.format(itbms);
       String to = df.format(total);
       
       txtSubtotal.setText(st);
       txtItbms.setText(i);
       txtTotal.setText(to);
       
     Limpiar1();
    }
    
   public void Eliminar(){
       int fila = Tabla.getSelectedRow();
       dtm.removeRow(fila);
        
       float fi=0;
       float Subtotal=0;
       float itbms=0,total=0;
       
       for(int i=0;i<Tabla.getRowCount();i++){
          fi = Float.parseFloat(Tabla.getValueAt(i, 4).toString());
          Subtotal += fi;
             
       }
       itbms=(float) (Subtotal*0.07);
       total=Subtotal+itbms;
       DecimalFormatSymbols s = new DecimalFormatSymbols();
     s.setDecimalSeparator('.');
      DecimalFormat df = new DecimalFormat("0.00",s);    
       
       String st = df.format(Subtotal);
       String i = df.format(itbms);
       String to = df.format(total);
       
       txtSubtotal.setText(st);
       txtItbms.setText(i);
       txtTotal.setText(to);
       
     txtCodigo.setText("0000");txtCantidad.setText("1");//limpio cajas de Cantidad y Codigo
   }
   
   public void Limpiar1(){
     txtCodigo.setText("0000");txtCantidad.setText("1");//limpio cajas de Cantidad y Codigo  
   }
   private void LimpiarTodo(){     //Metodo que se llama parsa limpiar Factura despues de creada
         txtCodigo.setText("0");         
         txtCantidad.setText("1");
         txtNombre.setText(null);
         txtSubtotal.setText("0.00");
         txtItbms.setText("0.00");
         txtTotal.setText("0.00");
         txtCedula.setText(null);
         txtSubtotal.setText("0.00");
         txtNfact.setText("");
         cbCondicion.setSelectedItem("..........");
         cbPago.setSelectedItem("....");
         
         int fila=dtm.getRowCount();
         for (int i = 0;i<fila; i++) {
         dtm.removeRow(0);
          }
         
         nfact();
   }
   
   public void Modificar(){
       int fila = Tabla.getSelectedRow();
       dtm.setValueAt(txtCantidad.getText(), fila, 3);
       
       Connection con = null; 
       
       
       int cf=0,ci=0;
      String canti = null, in = null, p=null;
      float pr=0;
       try{
         con = getConnection();
         
         
          for(int i=0;i<Tabla.getRowCount();i++){
             in = Tabla.getValueAt(i, 0).toString();
              ps = con.prepareStatement("SELECT * FROM inventario WHERE CodArticulo = ?");
              ps.setString(1, in);
              rs = ps.executeQuery();
                } 
           
           if(rs.next()){
              canti=rs.getString("Cantidad"); 
              p = rs.getString("Precio");
            
           }else{
               JOptionPane.showMessageDialog(null, "No Existe el Registro");
           }
            
           ci = Integer.parseInt(canti);// convierto cantidad de en invenratio a entero
            cf =Integer.parseInt(txtCantidad.getText());// Convierto cantidad a facturar a entero 
            
          
             
       if(ci>=cf){ 
               JOptionPane.showMessageDialog(null, "Inventario Verificado puede Continuar...");
           
           
           }else{
                JOptionPane.showMessageDialog(null, "No puedes Caer en Inventario Negativo");
                
              fila = Tabla.getSelectedRow();
               dtm.removeRow(fila);
            }
           
           con.close();     
        
         }catch(Exception e){
        System.out.println("Error");
         }
      
       float fi=0;
       float Subtotal=0;
       float itbms=0,total=0;
        
       pr= Float.parseFloat(p)*cf;
       
       dtm.setValueAt(pr, fila, 4);
       for(int i=0;i<Tabla.getRowCount();i++){
        
          fi = Float.parseFloat(Tabla.getValueAt(i, 4).toString());
          Subtotal += fi;
             
       }
       
       //dtm.setValueAt(fi, fila, 4);
       
       itbms=(float) (Subtotal*0.07);
       total=Subtotal+itbms;
       DecimalFormatSymbols s = new DecimalFormatSymbols();
     s.setDecimalSeparator('.');
      DecimalFormat df = new DecimalFormat("0.00",s);    
       
       String st = df.format(Subtotal);
       String i = df.format(itbms);
       String to = df.format(total);
       
       txtSubtotal.setText(st);
       txtItbms.setText(i);
       txtTotal.setText(to);
       
     txtCodigo.setText("0000");txtCantidad.setText("1");//limpio cajas de Cantidad y Codigo
   
   } 
   public void Guardar(){
       Connection con = null;
       int res=0;
       String Fecha = (cbAno.getSelectedItem().toString()+"-"+cbMes.getSelectedItem().toString()+"-"+cbDia.getSelectedItem().toString());
       try{
         
           con = getConnection(); 
        
        for(int i=0; i<Tabla.getRowCount();i++){
         ps = con.prepareStatement("INSERT INTO factura (CodArticulo, CantidadFact, fecha, Nombre, Cedula, SubTotal, Itbms, TotalFact, NumFact, Condicion, MetPago) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)");
         ps.setString(1, Tabla.getValueAt(i, 0).toString());
         ps.setInt(2, Integer.parseInt(Tabla.getValueAt(i, 3).toString()));
         ps.setDate(3, Date.valueOf(Fecha) );
        ps.setString(4, txtNombre.getText());
        ps.setString(5, txtCedula.getText());
        ps.setFloat(6, (float) Double.parseDouble(txtSubtotal.getText()));       
        ps.setFloat(7, (float) Double.parseDouble(txtItbms.getText()));
        ps.setFloat(8, (float) Double.parseDouble(txtTotal.getText()));
        ps.setInt(9, Integer.parseInt(txtNfact.getText()));
        ps.setString(10, cbCondicion.getSelectedItem().toString());
        ps.setString(11, cbPago.getSelectedItem().toString());
        res = ps.executeUpdate();
        }
       
        
        if(res>0){
          JOptionPane.showMessageDialog(null, "Factura Ingresada ");
         
         
        }
        else{
           JOptionPane.showMessageDialog(null, "Error al Ingresar Factura");
        }
        
       
        for(int i=0; i<Tabla.getRowCount();i++){
        ps = con.prepareStatement("UPDATE inventario set Cantidad = (Cantidad - ?) WHERE CodArticulo=?");
        ps.setInt(1, Integer.parseInt(Tabla.getValueAt(i, 3).toString()));
        ps.setString(2, Tabla.getValueAt(i, 0).toString());        
        res = ps.executeUpdate();
        }
        
        if(res>0){
          
          JOptionPane.showMessageDialog(null, "Inventario Modificado");
          imprimir();
          LimpiarTodo();
        
        }else{
           JOptionPane.showMessageDialog(null, "Error al Modificar Inventario");
         
        }
        
        con.close();
        
       }catch(Exception e){
        JOptionPane.showMessageDialog(null, e);
       }
       
   }
   
   public void imprimir(){
      try  
        { 
            PrinterJob job = PrinterJob.getPrinterJob(); //crea un trabajo de impresion que se asocia con la impresora predeterminada 
            job.setPrintable((Printable) this);//Se pasa la instancia del Formulario (JFrame) 
            boolean x = job.printDialog();//Se Abre el dialogo Para Imprimir 
            if (x == true) 
            { 
                job.print(); 
            }     
            else 
            { 
                //Se canceló la impresión 
            }     
        }  
        catch (Exception ex)  
        { 
            JOptionPane.showMessageDialog(null, "No Se Logró Imprimir Por El Siguiente Motivo" + ex); 
        } 
   }
   
   
   

    public Facturacion() {
        initComponents();
        //txtFecha.setText(FechaActual());   
       
        String[] titulo = new String[]{"CodArticulo","Descripcion","Precio","Cantidad","Total"};
        dtm.setColumnIdentifiers(titulo);
        Tabla.setModel(dtm);
        
        nfact();
    }

   
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtCedula = new javax.swing.JTextField();
        txtNombre = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        cbCondicion = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtNfact = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        cbPago = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        Tabla = new javax.swing.JTable();
        jLabel9 = new javax.swing.JLabel();
        txtCodigo = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        txtCantidad = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        txtSubtotal = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        txtItbms = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        txtTotal = new javax.swing.JTextField();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        cbDia = new javax.swing.JComboBox<>();
        cbMes = new javax.swing.JComboBox<>();
        cbAno = new javax.swing.JComboBox<>();
        jButton7 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(247, 242, 242));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("Factura Deportes Buda");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setText("Nombre:");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel3.setText("Cedula:");

        txtCedula.setToolTipText("Introduzca Cedula");

        txtNombre.setToolTipText("Introduzca Nombre");

        jButton1.setBackground(new java.awt.Color(204, 204, 255));
        jButton1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sistdeportesbuda/icons8-búsqueda-16.png"))); // NOI18N
        jButton1.setText("Buscar Cliente");
        jButton1.setToolTipText("Buscar Cliente por Numero de Cedula");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel4.setText("Condicion de Pago:");

        cbCondicion.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "..........", "Contado", "Credito" }));
        cbCondicion.setToolTipText("Ingrese la Condicion de Pago");

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sistdeportesbuda/icons8-flecha-derecha-larga-16.png"))); // NOI18N

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel6.setText("Fecha:");
        jLabel6.setToolTipText("");

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel7.setText("Factura #:");

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel8.setText("Metodo de Pago:");

        cbPago.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "....", "Efectivo", "Clave", "Visa", "Mastercard", "Cheque", "No Pago(Fact. Credito)" }));
        cbPago.setToolTipText("Ingrese el Metodo de Pago");

        Tabla.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(Tabla);

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel9.setText("Introduzca Codigo de Aticulo:");

        txtCodigo.setText("0000");
        txtCodigo.setToolTipText("Ingrese Codigo de Producto");

        jButton2.setBackground(new java.awt.Color(204, 204, 255));
        jButton2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sistdeportesbuda/icons8-búsqueda-16.png"))); // NOI18N
        jButton2.setText("Buscar Codigo");
        jButton2.setToolTipText("Buscar Codigo de los Articulos");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel10.setText("Introduzca Cantidad:");

        txtCantidad.setText("1");
        txtCantidad.setToolTipText("Ingrese Cantidad de Articulo");

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sistdeportesbuda/icons8-flecha-derecha-larga-16.png"))); // NOI18N

        jButton3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sistdeportesbuda/icons8-agregar-a-carrito-de-compras-16.png"))); // NOI18N
        jButton3.setText("Agregar Articulo a Factura");
        jButton3.setToolTipText("Agregar Articulo a la Tabla");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel12.setText("Vista de la Compra:");

        jButton4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sistdeportesbuda/icons8-guardar-16.png"))); // NOI18N
        jButton4.setText("Crear Factura:");
        jButton4.setToolTipText("Ingresa e imprime la Factura");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel13.setText("SubTotal:");

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel14.setText("ITBMS:");

        txtItbms.setToolTipText("");

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel15.setText("Total:");

        jButton5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sistdeportesbuda/icons8-salir-redondeado-16.png"))); // NOI18N
        jButton5.setText("Salir");
        jButton5.setToolTipText("Salir de la Factura");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sistdeportesbuda/icons8-eliminar-16 (1).png"))); // NOI18N
        jButton6.setText("Eliminar Producto de la Factura");
        jButton6.setToolTipText("Eliminar Articulo de la Tabla");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        cbDia.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31" }));
        cbDia.setToolTipText("Dia");

        cbMes.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", " " }));
        cbMes.setToolTipText("Mes");

        cbAno.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "2019", "2020", "2021", "2022", " " }));
        cbAno.setToolTipText("Ano");

        jButton7.setBackground(new java.awt.Color(204, 204, 255));
        jButton7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sistdeportesbuda/icons8-actualizar-16.png"))); // NOI18N
        jButton7.setText("Modificar Cantidad de Articulo");
        jButton7.setToolTipText("Modificar Cantidad de Articulo en la tabla");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtCedula, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addComponent(cbCondicion, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cbPago, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton6, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jLabel10)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(237, 237, 237)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(54, 54, 54))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(95, 95, 95)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton1)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel6)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(31, 31, 31)
                                .addComponent(jLabel12))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cbDia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cbMes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(cbAno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtNfact, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(349, 349, 349)
                .addComponent(jLabel1)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(51, 51, 51)
                .addComponent(jButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 293, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(80, 80, 80)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel15)
                            .addComponent(jLabel14)))
                    .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(txtTotal, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(txtItbms, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE))
                    .addComponent(txtSubtotal, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(108, 108, 108))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(28, 28, 28)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtCedula, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton1)
                        .addComponent(jLabel6)
                        .addComponent(cbDia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbMes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbAno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7)
                        .addComponent(txtNfact, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(cbCondicion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(cbPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(62, 62, 62)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton2)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel9)
                                .addComponent(jLabel11)))
                        .addGap(18, 18, 18)
                        .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(34, 34, 34)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(txtCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(34, 34, 34)
                        .addComponent(jButton3)
                        .addGap(18, 18, 18)
                        .addComponent(jButton7))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jButton6)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addGap(25, 25, 25)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel13)
                                .addComponent(txtSubtotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtItbms, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel14))
                            .addGap(18, 18, 18)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel15)
                                .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(44, 44, 44))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton5)
                            .addGap(70, 70, 70)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(70, 70, 70))))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
     
                                      
       ListaCodigosProductos l = new ListaCodigosProductos();  
       l.setVisible(true);
        
        
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
          agregar();        
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
         Connection con = null;
        
        try{
           con = getConnection();
           ps = con.prepareStatement("SELECT * FROM cliente WHERE NCedula = ?");
           ps.setString(1, txtCedula.getText());
           rs = ps.executeQuery();
           
           if(rs.next()){
            txtNombre.setText(rs.getString("Nombre"));
            
            
           }else{
               JOptionPane.showMessageDialog(null, "No Existe el Registro, Favor Agregar Cliente");
               RegistroCliente r = new RegistroCliente();
               r.setVisible(true);
           } 
        }catch(Exception e){
            System.out.println("Error de Conexion");
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        Eliminar();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        Guardar();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
        Modificar();
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        
        this.setVisible(false);
    }//GEN-LAST:event_jButton5ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Facturacion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Facturacion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Facturacion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Facturacion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new Facturacion().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable Tabla;
    private javax.swing.JComboBox<String> cbAno;
    private javax.swing.JComboBox<String> cbCondicion;
    private javax.swing.JComboBox<String> cbDia;
    private javax.swing.JComboBox<String> cbMes;
    private javax.swing.JComboBox<String> cbPago;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField txtCantidad;
    private javax.swing.JTextField txtCedula;
    private javax.swing.JTextField txtCodigo;
    private javax.swing.JTextField txtItbms;
    private javax.swing.JTextField txtNfact;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtSubtotal;
    private javax.swing.JTextField txtTotal;
    // End of variables declaration//GEN-END:variables

    @Override
    public int print(Graphics graf, PageFormat pagfor, int index) throws PrinterException {
     if(index>0){
        return NO_SUCH_PAGE; 
     }
     Graphics2D hub = (Graphics2D) graf;
     hub.translate(pagfor.getImageableX() + 30, pagfor.getImageableY() + 30);
     hub.scale(0.6, 0.6);
     jPanel2.printAll(graf);
        return PAGE_EXISTS ;
    }

 
}
