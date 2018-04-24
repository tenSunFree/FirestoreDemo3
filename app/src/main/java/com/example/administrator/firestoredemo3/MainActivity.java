package com.example.administrator.firestoredemo3;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class MainActivity extends AppCompatActivity {

    private String nameString;
    private String rewardsString;
    private EditText nameEditText;
    private EditText rewardsEditText;
    private TextView nameTextView;
    private TextView rewardsTextView;
    private OneOfThe oneOfThe;

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private DocumentReference documentReference = firebaseFirestore.document("ListOfRewards/01");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameEditText = findViewById(R.id.nameEditText);
        rewardsEditText = findViewById(R.id.rewardsEditText);
        nameTextView = findViewById(R.id.nameTextView);
        rewardsTextView = findViewById(R.id.rewardsTextView);

        loadNote();
    }

    /**
     * Activity的可見生命週期是介於 onStart() 呼叫和 onStop() 呼叫之間
     */
    @Override
    protected void onStart() {
        super.onStart();

        /** 監聽指定位置的資料內容, 如果有變化 就即時更新 */
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(MainActivity.this, "Error while loading!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (documentSnapshot.exists()) {
                    if (oneOfThe != null) {
                        nameTextView.setText(oneOfThe.getName());
                        rewardsTextView.setText(oneOfThe.getRewards());
                    }
                } else {
                    nameTextView.setText("");
                    rewardsTextView.setText("");
                }
            }
        });
    }

    public void saveNote(View view) {
        nameString = nameEditText.getText().toString();
        rewardsString = rewardsEditText.getText().toString();
        oneOfThe = new OneOfThe(nameString, rewardsString);
        documentReference.set(oneOfThe)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this, "saveNote(), onSuccess()", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "saveNote(), onFailure()", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void loadNote() {
        documentReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            oneOfThe = documentSnapshot.toObject(OneOfThe.class);
                            nameString = oneOfThe.getName();
                            rewardsString = oneOfThe.getRewards();
                            nameTextView.setText(nameString);
                            rewardsTextView.setText(rewardsString);
                        } else {
                            Toast.makeText(MainActivity.this, "loadNote(), onSuccess(), not exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "loadNote(), onFailure()", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
