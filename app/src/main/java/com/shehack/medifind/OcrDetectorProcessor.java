/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.shehack.medifind;

import android.content.Intent;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.shehack.medifind.ui.camera.GraphicOverlay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

//import com.google.android.gms.samples.vision.ocrreader.ui.camera.GraphicOverlay;

/**
 * A very simple Processor which gets detected TextBlocks and adds them to the overlay
 * as OcrGraphics.
 */
public class OcrDetectorProcessor implements Detector.Processor<TextBlock> {

    private GraphicOverlay<OcrGraphic> graphicOverlay;
    OcrCaptureActivity ocr;
    static HashMap<String,Integer> map = new HashMap<>();
    int max_val;
    OcrDetectorProcessor(GraphicOverlay<OcrGraphic> ocrGraphicOverlay, OcrCaptureActivity ocrCaptureActivity) {
        graphicOverlay = ocrGraphicOverlay;
        max_val = 0;
        ocr = ocrCaptureActivity;
    }

    /**
     * Called by the detector to deliver detection results.
     * If your application called for it, this could be a place to check for
     * equivalent detections by tracking TextBlocks that are similar in location and content from
     * previous frames, or reduce noise by eliminating TextBlocks that have not persisted through
     * multiple detections.
     */

    public static <C> List<C> asList(SparseArray<C> sparseArray) {
        if (sparseArray == null) return null;
        List<C> arrayList = new ArrayList<C>(sparseArray.size());
        for (int i = 0; i < sparseArray.size(); i++)
            arrayList.add(sparseArray.valueAt(i));
        return arrayList;
    }

    public static Comparator<TextBlock> TextBlockComparator
            = new Comparator<TextBlock>() {
        public int compare(TextBlock textBlock1, TextBlock textBlock2) {
            float comp1 = textBlock1.getBoundingBox().width()/textBlock1.getBoundingBox().height();
            float comp2 = textBlock2.getBoundingBox().width()/textBlock2.getBoundingBox().height();
            return (int)(comp2-comp1);
        }
    };

    @Override
    public void receiveDetections(Detector.Detections<TextBlock> detections) {
        graphicOverlay.clear();
        SparseArray<TextBlock> items = detections.getDetectedItems();
        List<TextBlock> list = asList(items);
        Collections.sort(list,TextBlockComparator);
        for (int i = 0; i < 3; ++i) {
            TextBlock item = list.get(i);
            if (item != null && item.getValue() != null) {
                if(map.containsKey(item.getValue())){
                    map.replace(item.getValue(),map.get(item.getValue())+1);
                    if(map.get(item.getValue())>max_val) max_val = map.get(item.getValue());
                }
                else map.put(item.getValue(),1);
                Log.d("OcrDetectorProcessor", "Text detected! " + i + " " + item.getValue());
                OcrGraphic graphic = new OcrGraphic(graphicOverlay, item);
                graphicOverlay.add(graphic);
            }
        }
        if(max_val>100){
            Set<Map.Entry<String, Integer>> entries = map.entrySet();

            Comparator<Map.Entry<String, Integer>> valueComparator = new Comparator<Map.Entry<String,Integer>>() {

                @Override
                public int compare(Map.Entry<String, Integer> e1, Map.Entry<String, Integer> e2) {
                    Integer v1 = e1.getValue();
                    Integer v2 = e2.getValue();
                    return v1.compareTo(v2);
                }
            };
            List<Map.Entry<String, Integer>> listOfEntries = new ArrayList<Map.Entry<String, Integer>>(entries);
            Collections.sort(listOfEntries, valueComparator);
            int i=0;
            String res1=null,res2 = null;
            for(Map.Entry<String, Integer> entry : listOfEntries){
                if(i==2){
                    max_val = 0;
                    map.clear();
                    Intent intent = new Intent(ocr,ResultsActivity.class).putExtra("res1",res1.toLowerCase());
                    intent.putExtra("res2",res2.toLowerCase());
                    ocr.startActivity(intent);
                }
                if(i==0) res1 = entry.getKey();
                else if(i==1) res2 = entry.getKey();
                i++;
            }

        }
    }

    /**
     * Frees the resources associated with this detection processor.
     */
    @Override
    public void release() {
        graphicOverlay.clear();
    }
}
