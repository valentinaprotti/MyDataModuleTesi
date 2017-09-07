package com.example.android.mydata_prova.model.MyData;

import com.example.android.mydata_prova.model.registry.Metadata;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Set;

/**
 * Created by Valentina on 25/08/2017.
 */

public class PersonalDataVault implements IPersonalDataVault {
    String datoUno;
    int datoDue;

    public PersonalDataVault() {
        this.datoUno = readDatoUno();
        this.datoDue = readDatoDue();
    }

    private int readDatoDue() {
        String fileName = "DatiProva.txt";
        int result = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line;


            while ((line = br.readLine()) != null) {
                if (line == null || line.length() == 0)
                    throw new IllegalArgumentException("line must be initialized");
                result = Integer.parseInt(line.split(" ")[0]);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private String readDatoUno() {
        String fileName = "DatiProva.txt";
        String result = new String();
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line;


            while ((line = br.readLine()) != null) {
                if (line == null || line.length() == 0)
                    throw new IllegalArgumentException("line must be initialized");
                result = line.split(" ")[1];
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public IDataSet getData(Set<String> typesConst) {
        return null;
    }

    @Override
    public void saveData(IDataSet dataSet) {
        for (String typeConst : dataSet.getKeys()) {
            if (typeConst.equals(Metadata.DATOUNOPROVA_CONST))
                this.setDatoUno((String) dataSet.getObject(typeConst));
            if (typeConst.equals(Metadata.DATODUEPROVA_CONST))
                this.setDatoDue((int) dataSet.getObject(typeConst));
        }
    }

    public void setDatoUno(String datoUno) {
        this.datoUno = datoUno;
    }

    public void setDatoDue(int datoDue) {
        this.datoDue = datoDue;
    }
}
