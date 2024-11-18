package com.example.finalproject;

import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.example.finalproject.databinding.ActivityOfflineMapBinding;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.rendertheme.InternalRenderTheme;
import java.io.FileInputStream;
import java.io.IOException;

public class Offline_Map extends AppCompatActivity {

    private static final LatLong UIT = new LatLong(10.869811, 106.803065);
    private static final LatLong LANDMARK81 = new LatLong(10.794684, 106.719896);

    private ActivityOfflineMapBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidGraphicFactory.createInstance(getApplication());

        b = ActivityOfflineMapBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        ActivityResultLauncher<Intent> contract = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result != null && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            openMap(uri);
                        }
                    }
                }
        );

        b.openMap.setOnClickListener(v -> contract.launch(
                new Intent(Intent.ACTION_OPEN_DOCUMENT)
                        .setType("*/*")
                        .addCategory(Intent.CATEGORY_OPENABLE)
        ));
    }

    private void openMap(Uri uri) {
        b.map.getMapScaleBar().setVisible(true);
        b.map.setBuiltInZoomControls(true);
        TileCache cache = AndroidUtil.createTileCache(
                this,
                "mycache",
                b.map.getModel().displayModel.getTileSize(),
                1f,
                b.map.getModel().frameBufferModel.getOverdrawFactor()
        );

        try (FileInputStream stream = (FileInputStream) getContentResolver().openInputStream(uri)) {
            MapFile mapStore = new MapFile(stream);

            TileRendererLayer renderLayer = new TileRendererLayer(
                    cache,
                    mapStore,
                    b.map.getModel().mapViewPosition,
                    AndroidGraphicFactory.INSTANCE
            );

            renderLayer.setXmlRenderTheme(InternalRenderTheme.DEFAULT);

            b.map.getLayerManager().getLayers().add(renderLayer);

            b.map.setCenter(UIT);
            b.map.setZoomLevel((byte) 10); // Convert int to byte
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
