package com.trodev.petdiary.activity;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.trodev.petdiary.adapter.NotesAdapter;
import com.trodev.petdiary.model.Note;
import com.trodev.petdiary.R;
import com.trodev.petdiary.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    FloatingActionButton addNoteBtn;
    private final static float CLICK_DRAG_TOLERANCE = 10;
    RecyclerView recyclerView;
    RelativeLayout NoDataFoundLayout;

    private List<Note> noteList;
    private NotesAdapter notesAdapter;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference, reference;
    TextView t1;
    String uID, users;
    FirebaseUser user;
    ProgressBar progress_circular;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*init textview */
        t1 = findViewById(R.id.t1);
        progress_circular = findViewById(R.id.progress_circular);
        progress_circular.setProgressTintList(ColorStateList.valueOf(Color.RED));


        initialization();

        loadDataFromFirebase();

        addNoteBtn.setOnClickListener((v) -> startActivity(new Intent(MainActivity.this, NoteDetailsActivity.class)));

//-----------------movable floating btn ----start-------------------------------------
        addNoteBtn.setOnTouchListener(new View.OnTouchListener() {

            private float downRawX, downRawY;
            private float dX, dY;

            public boolean onTouch(View view, MotionEvent motionEvent) {

                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();

                int action = motionEvent.getAction();
                if (action == MotionEvent.ACTION_DOWN) {

                    downRawX = motionEvent.getRawX();
                    downRawY = motionEvent.getRawY();
                    dX = view.getX() - downRawX;
                    dY = view.getY() - downRawY;

                    return true; // Consumed

                } else if (action == MotionEvent.ACTION_MOVE) {

                    int viewWidth = view.getWidth();
                    int viewHeight = view.getHeight();

                    View viewParent = (View) view.getParent();
                    int parentWidth = viewParent.getWidth();
                    int parentHeight = viewParent.getHeight();

                    float newX = motionEvent.getRawX() + dX;
                    newX = Math.max(layoutParams.leftMargin, newX); // Don't allow the FAB past the left hand side of the parent
                    newX = Math.min(parentWidth - viewWidth - layoutParams.rightMargin, newX); // Don't allow the FAB past the right hand side of the parent

                    float newY = motionEvent.getRawY() + dY;
                    newY = Math.max(layoutParams.topMargin, newY); // Don't allow the FAB past the top of the parent
                    newY = Math.min(parentHeight - viewHeight - layoutParams.bottomMargin, newY); // Don't allow the FAB past the bottom of the parent

                    view.animate()
                            .x(newX)
                            .y(newY)
                            .setDuration(0)
                            .start();

                    return true; // Consumed

                } else if (action == MotionEvent.ACTION_UP) {

                    float upRawX = motionEvent.getRawX();
                    float upRawY = motionEvent.getRawY();

                    float upDX = upRawX - downRawX;
                    float upDY = upRawY - downRawY;

                    //================================================================================================
                    if (Math.abs(upDX) < CLICK_DRAG_TOLERANCE && Math.abs(upDY) < CLICK_DRAG_TOLERANCE) { // A click
                        return addNoteBtn.performClick();
                    } else { // A drag
                        return true; // Consumed
                    }
                    //================================================================================================


                }
                return false;
            }


        });
//-----------------movable floating btn ----end-------------------------------------


        navigationView.setItemIconTintList(null);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();


                if (id == R.id.logout) {

                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();

                    drawerLayout.closeDrawers();
                } else if (id == R.id.share) {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    String shareSubText = "All In One Status App Checkout Caption and Quote For Absolutely Free  ";
                    String shareBodyText = "https://play.google.com/store/apps/details?id=" + R.string.appPackageName;
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubText);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareBodyText);
                    startActivity(Intent.createChooser(shareIntent, "Share With"));

                } else if (id == R.id.moreapp) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://play.google.com/store/apps/dev?id=6580660399707616800"));
                    startActivity(intent);


                } else if (id == R.id.update) {
                    Intent intentc;
                    intentc = new Intent(Intent.ACTION_VIEW);
                    intentc.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + R.string.appPackageName));
                    startActivity(intentc);

                } else if (id == R.id.rateus) {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + R.string.appPackageName)));
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + R.string.appPackageName)));
                    }
                }

                return false;
            }
        });


    }
    //------------------onCreate end here ---------------------------------------------------------


    void initialization() {

        NoDataFoundLayout = findViewById(R.id.NoDataFoundLayout);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.navigationView);
        drawerLayout = findViewById(R.id.drawerLayout);
        addNoteBtn = findViewById(R.id.add_note_btn);
        recyclerView = findViewById(R.id.recyler_view);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        noteList = new ArrayList<>();
        notesAdapter = new NotesAdapter(this, noteList);
        recyclerView.setAdapter(notesAdapter);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    void loadDataFromFirebase() {

        uID = firebaseAuth.getCurrentUser().getUid();

        Query noteRef = databaseReference.child("Notes").child(uID).orderByChild("timestamp");

        noteRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                noteList.clear();
                progress_circular.setVisibility(View.VISIBLE);
                for (DataSnapshot doc : snapshot.getChildren()) {
                    Note note = doc.getValue(Note.class);
                    noteList.add(note);
                    progress_circular.setVisibility(View.INVISIBLE);
                }

                notesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    //-----tool bar method --start --------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.toolbar_menu, menu);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menu) {


            if (!drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.openDrawer(GravityCompat.END);
            }

        }

        return true;
    }

}