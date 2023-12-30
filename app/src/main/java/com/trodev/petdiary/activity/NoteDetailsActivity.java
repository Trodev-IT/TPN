package com.trodev.petdiary.activity;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.trodev.petdiary.model.Note;
import com.trodev.petdiary.R;
import com.trodev.petdiary.util.Utility;

import java.io.IOException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;


public class NoteDetailsActivity extends AppCompatActivity {

    TextInputEditText titleEditText, contentEditText, notes_age_text, notes_food_text, notes_feeding_scheduled_text,
            notes_walking_Time_txt, notes_medicine_txt, notes_Descriptions_txt, notes_ownerName_txt,
            notes_address_txt, notes_phone_txt, notes_txt;

    private CircleImageView profileIV;
    TextView saveNoteBtn, page_titles;
    TextView pageTitleTextView;
    String title, content, nID, age, address, description, feedingTime, food, medicine, notes, ownerName, phone, walkingTime, photoUrl;
    boolean isEditMode = false;
    TextView deleteNoteTextViewBtn;
    ImageView notePhoto, deleteImg, uplaodImg;
    Uri ImageUri;
    StorageReference mStoreageref;

    private String imageDownloadLink;
    private Uri imageUri;
    private static final int REQUEST_CODE = 1;
    private StorageReference storageReference;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        initialization();

        saveNoteBtn.setOnClickListener((v) -> saveNote());

        page_titles = findViewById(R.id.page_titles);

        deleteNoteTextViewBtn.setOnClickListener((v) -> deleteNoteFromFirebase());

    }

    void initialization() {

        profileIV = findViewById(R.id.profileIV);
        titleEditText = findViewById(R.id.notes_title_text);
        contentEditText = findViewById(R.id.notes_content_text);
        notes_age_text = findViewById(R.id.notes_age_text);
        notes_food_text = findViewById(R.id.notes_food_text);
        notes_feeding_scheduled_text = findViewById(R.id.notes_feeding_scheduled_text);
        notes_walking_Time_txt = findViewById(R.id.notes_walking_Time_txt);
        notes_address_txt = findViewById(R.id.notes_address_txt);
        notes_medicine_txt = findViewById(R.id.notes_medicine_txt);
        notes_Descriptions_txt = findViewById(R.id.notes_Descriptions_txt);
        notes_ownerName_txt = findViewById(R.id.notes_ownerName_txt);
        notes_phone_txt = findViewById(R.id.notes_phone_txt);
        notes_txt = findViewById(R.id.notes_txt);
        saveNoteBtn = findViewById(R.id.save_note_btn);
        pageTitleTextView = findViewById(R.id.page_title);
        deleteNoteTextViewBtn = findViewById(R.id.delete_note_text_view_btn);


        //receive data
        nID = getIntent().getStringExtra("nID");
        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        age = getIntent().getStringExtra("age");
        address = getIntent().getStringExtra("address");
        description = getIntent().getStringExtra("description");
        feedingTime = getIntent().getStringExtra("feedingTime");
        food = getIntent().getStringExtra("food");
        medicine = getIntent().getStringExtra("medicine");
        notes = getIntent().getStringExtra("notes");
        ownerName = getIntent().getStringExtra("ownerName");
        phone = getIntent().getStringExtra("phone");
        walkingTime = getIntent().getStringExtra("walkingTime");
        photoUrl = getIntent().getStringExtra("photoUrl");

        if (photoUrl != null && !photoUrl.equals("")) {
            Glide.with(this).load(photoUrl).into(profileIV);
        }


        if (nID != null && !nID.isEmpty()) {
            isEditMode = true;
        }


        titleEditText.setText(title);
        contentEditText.setText(content);
        notes_age_text.setText(age);
        notes_address_txt.setText(address);
        notes_Descriptions_txt.setText(description);
        notes_feeding_scheduled_text.setText(feedingTime);
        notes_food_text.setText(food);
        notes_medicine_txt.setText(medicine);
        notes_txt.setText(notes);
        notes_ownerName_txt.setText(ownerName);
        notes_phone_txt.setText(phone);
        notes_walking_Time_txt.setText(walkingTime);


        if (isEditMode) {
            pageTitleTextView.setText("Edit pet profile");
            deleteNoteTextViewBtn.setVisibility(View.VISIBLE);
            // page_titles.setText("Tap on image to update Image");
        }


        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    public void chooseImage(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            imageUri = data.getData();
            if (imageUri != null) {
                Glide.with(this).load(imageUri).into(profileIV);
            }
        }
    }

    void saveNote() {

        String noteTitle = titleEditText.getText().toString();
        String noteContent = contentEditText.getText().toString();
        String noteAge = notes_age_text.getText().toString();
        String noteFood = notes_food_text.getText().toString();
        String noteFeedingTime = notes_feeding_scheduled_text.getText().toString();
        String noteWalkingTime = notes_walking_Time_txt.getText().toString();
        String noteMedicine = notes_medicine_txt.getText().toString();
        String noteDescriptions = notes_Descriptions_txt.getText().toString();
        String noteOwnerName = notes_ownerName_txt.getText().toString();
        String noteAddress = notes_address_txt.getText().toString();
        String notePhone = notes_phone_txt.getText().toString();
        String noteNotes = notes_txt.getText().toString();


        if (imageUri == null) {
            Utility.showToast(this, "Need to choose image");
        } else if (noteTitle.isEmpty()) {
            titleEditText.setError("Title is required");
        } else if (noteContent.isEmpty()) {
            contentEditText.setError("Species is required");
            return;
        } else if (noteDescriptions.isEmpty()) {
            notes_Descriptions_txt.setError("Description is required");
            return;
        } else {

            Note note = new Note();
            note.setTitle(noteTitle);
            note.setContent(noteContent);
            note.setTimestamp(System.currentTimeMillis());
            note.setAge(noteAge);
            note.setFood(noteFood);
            note.setFeedingTime(noteFeedingTime);
            note.setWalkingTime(noteWalkingTime);
            note.setMedicine(noteMedicine);
            note.setDescription(noteDescriptions);
            note.setOwnerName(noteOwnerName);
            note.setAddress(noteAddress);
            note.setPhone(notePhone);
            note.setNotes(noteNotes);
            note.setPhotoUrl(photoUrl);

            saveNoteToFirebase(note);
        }

    }

    void saveNoteToFirebase(Note note) {

        String uID = firebaseAuth.getCurrentUser().getUid();

        DatabaseReference noteRef = databaseReference.child("Notes").child(uID);

        if (isEditMode) {
            uploadImageAndSaveData(uID, nID, note, noteRef);
        } else {
            String noteID = databaseReference.push().getKey();
            uploadImageAndSaveData(uID, noteID, note, noteRef);
        }

    }


    void uploadImageAndSaveData(String uID, String noteID, Note note, DatabaseReference noteRef) {

        if (imageUri != null) {
            StorageReference imageRef = storageReference.child("notes").child(uID).child(noteID + ".jpg");

            UploadTask uploadTask = imageRef.putFile(imageUri);
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    // Continue with the task to get the download URL
                    return imageRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {

                        Utility.showToast(NoteDetailsActivity.this, isEditMode ? "Image Updated Successfully." : "Image Upload Successfully.");

                        Uri downloadUri = task.getResult();
                        imageDownloadLink = downloadUri.toString();

                        note.setnID(noteID);

                        note.setPhotoUrl(imageDownloadLink);

                        assert noteID != null;
                        noteRef.child(noteID).setValue(note).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Utility.showToast(NoteDetailsActivity.this, isEditMode ? "Note Updated" : "Note Added!");
                                    finish();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Utility.showToast(NoteDetailsActivity.this, e.getMessage());
                            }
                        });


                    }
                }
            });
        } else if (isEditMode) {

            note.setnID(noteID);

            note.setPhotoUrl(photoUrl);
            noteRef.child(noteID).setValue(note).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Utility.showToast(NoteDetailsActivity.this, "Note Updated!");
                        finish();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Utility.showToast(NoteDetailsActivity.this, e.getMessage());
                }
            });

        }

    }

    void deleteNoteFromFirebase() {

        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to delete?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String uID = firebaseAuth.getCurrentUser().getUid();

                        DatabaseReference noteRef = databaseReference.child("Notes").child(uID);

                        noteRef.child(nID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                StorageReference photoRef = storageReference.child("notes").child(uID).child(nID + ".jpg");

                                photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Utility.showToast(NoteDetailsActivity.this, "Image Deleted!");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Utility.showToast(NoteDetailsActivity.this, "Failed to delete image");
                                    }
                                });

                                Utility.showToast(NoteDetailsActivity.this, "Deleted successfully");
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Utility.showToast(NoteDetailsActivity.this, "Failed while deleting data");
                            }
                        });

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Dismiss the dialog and continue
                        dialog.dismiss();
                    }
                })
                .show();

    }

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null && data.getData() != null) {
                        ImageUri = data.getData();

                        try {
                            InputStream inputStream = getContentResolver().openInputStream(ImageUri);

                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inJustDecodeBounds = true;
                            BitmapFactory.decodeStream(inputStream, null, options);
                            inputStream.close();

                            final int maxSize = 1024;
                            int scale = 1;
                            while (options.outWidth / scale >= maxSize || options.outHeight / scale >= maxSize) {
                                scale *= 2;
                            }

                            options = new BitmapFactory.Options();
                            options.inSampleSize = scale;
                            inputStream = getContentResolver().openInputStream(ImageUri);
                            Bitmap resizedBitmap = BitmapFactory.decodeStream(inputStream, null, options);
                            inputStream.close();

                            if (resizedBitmap != null) {
                                notePhoto.setImageBitmap(resizedBitmap);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );


    //make a method to upload image into firebase storage
    private void UploadImage() {
        saveNote();
        //check Imageuri
        if (ImageUri != null) {
            //create storage Instance
            final StorageReference myRef = mStoreageref.child("photo/" + ImageUri.getLastPathSegment());
            myRef.putFile(ImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    //here we need to get download Url to store in String
                    myRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {


                            if (uri != null) {
                                photoUrl = uri.toString();
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Utility.showToast(NoteDetailsActivity.this, e.getMessage());
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Utility.showToast(NoteDetailsActivity.this, e.getMessage());
                }
            });

        }
    }
}
