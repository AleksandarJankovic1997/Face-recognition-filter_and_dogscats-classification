package com.example.vestackainteligencija1;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.automl.AutoMLImageLabelerLocalModel;
import com.google.mlkit.vision.label.automl.AutoMLImageLabelerOptions;

import java.util.List;


public class MyCustomModelMLKITFragment extends Fragment {

    private AutoMLImageLabelerLocalModel localModel;
    public static final int  PICK_PHOTO=1;
    private Bitmap photo;

    private Button chooseImageButton;
    private ImageView imageView;
    private TextView textView;
    private TextView textView2;
    
    public MyCustomModelMLKITFragment() {
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_my_custom_model_m_l_k_i_t,container,false);
        this.chooseImageButton=v.findViewById(R.id.image_from_gallery_button);
        this.imageView=v.findViewById(R.id.custom_model_image_view);
        this.textView=v.findViewById(R.id.text_result1);
        this.textView2=v.findViewById(R.id.text_result2);

        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select picture"),PICK_PHOTO);
            }
        });
        return v;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri selectedImage=null;
        if(data!=null){
            selectedImage=data.getData();

            try {
                photo = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
            }catch (Exception e){
                Log.d("greska","greska");
                return;
            }
        }
        try{
            ExifInterface exif=new ExifInterface(getActivity().getContentResolver().openInputStream(selectedImage));
            int orientation=exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,1);
            Matrix matrix=new Matrix();
            if(orientation==6){
                matrix.postRotate(90);
            }
            else if(orientation==3){
                matrix.postRotate(180);
            }
            else if(orientation==8){
                matrix.postRotate(270);
            }
            photo =Bitmap.createBitmap(photo,0,0, photo.getWidth(),photo.getHeight(),matrix,true);

        }catch(Exception e){

        }
        this.imageView.setImageBitmap(photo);
        this.imageView.setVisibility(View.VISIBLE);
        setUpFirebase(photo);
    }
    public void setUpFirebase(Bitmap bitmap){
        localModel=new AutoMLImageLabelerLocalModel.Builder()
                .setAssetFilePath("model/manifest.json")
                .build();
        AutoMLImageLabelerOptions autoMLImageLabelerOptions=new AutoMLImageLabelerOptions.Builder(localModel)
                .setConfidenceThreshold(0.5f)
                .build();
        ImageLabeler imageLabeler= ImageLabeling.getClient(autoMLImageLabelerOptions);
        InputImage image=InputImage.fromBitmap(photo,0);
        imageLabeler.process(image)
                .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                    @Override
                    public void onSuccess(List<ImageLabel> imageLabels) {

                        ImageLabel label=imageLabels.get(0);
                            String text=label.getText();
                            float confidence=label.getConfidence();
                            int index=label.getIndex();
                            textView.setText(text+"confidence"+confidence+"----"+index);
                            textView.setVisibility(View.VISIBLE);
                            textView.setTextSize(20);

                            ImageLabel label2=imageLabels.get(1);
                        String text2=label2.getText();
                        float confidence2=label2.getConfidence();
                        int index2=label2.getIndex();
                        textView2.setText(text2+"confidence"+confidence2+"----"+index2);
                        textView2.setVisibility(View.VISIBLE);
                        textView2.setTextSize(20);


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }
}