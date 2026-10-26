package com.example.fireb;

import static com.example.fireb.FBRef.refAuth;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    EditText et1,et2;
    Button bt;
    ImageButton btE;
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et1=findViewById(R.id.et1);
        et2=findViewById(R.id.et2);
        bt=findViewById(R.id.bt);
        btE=findViewById(R.id.btE);

        tv=findViewById(R.id.tv);

        btE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et2.getInputType() == 129) {
                    et2.setInputType(1);
                } else {
                    et2.setInputType(129);
                }
            }
        });
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = et1.getText().toString();
                String pass = et2.getText().toString();

                if (email.isEmpty() || pass.isEmpty())
                    tv.setText("עלייך להכניס את כל הפרטים!");

                else {

                    ProgressDialog pd = new ProgressDialog(MainActivity.this); // הודעה שהמשתמש מתחבר
                    pd.setTitle("מתחבר");
                    pd.setMessage("יוצר משתמש...");
                    pd.show();

                    // יצירת משתמש חדש ב-Firebase באמצעות אימייל וסיסמה
                    refAuth.createUserWithEmailAndPassword(email, pass)

                            // הוספת פעולה שתתבצע כשהפעולה תסתיים
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                                @Override
                                // פעולה שנקראת כשהניסיון ליצור משתמש מסתיים
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    pd.dismiss(); // סגירת חלונית ההמתנה

                                    // בדיקה האם יצירת המשתמש הצליחה
                                    if (task.isSuccessful()) {

                                        // הדפסת הודעת הצלחה ב-Logcat
                                        Log.i("MainActivity", "createUserWithEmailAndPassword:success");

                                        // קבלת המשתמש המחובר כרגע
                                        FirebaseUser user = refAuth.getCurrentUser();

                                        // הצגת הודעת הצלחה והמזהה הייחודי של המשתמש
                                        tv.setText("המשתמש נוצר בהצלחה! " + user.getUid());
                                    }

                                    // אם יצירת המשתמש נכשלה
                                    else {

                                        // קבלת פרטי השגיאה שהתרחשה
                                        Exception exp = task.getException();

                                        // בדיקה האם השגיאה קשורה למשתמש שאינו תקין או לא קיים
                                        if (exp instanceof FirebaseAuthInvalidUserException)
                                            tv.setText("אימייל לא חוקי"); // הצגת הודעה על אימייל לא חוקי

                                            // בדיקה האם הסיסמה חלשה מדי
                                        else if (exp instanceof FirebaseAuthWeakPasswordException)
                                            tv.setText("סיסמה חלשה"); // הצגת הודעה על סיסמה חלשה

                                            // בדיקה האם המשתמש כבר קיים במערכת
                                        else if (exp instanceof FirebaseAuthUserCollisionException) {
                                            tv.setText("המשתמש כבר קיים"); // הצגת הודעה שהמשתמש כבר קיים
                                        }

                                        // בדיקה האם פרטי ההתחברות אינם תקינים
                                        else if (exp instanceof FirebaseAuthInvalidCredentialsException) {
                                            tv.setText("כשל אימות כללי"); // הצגת הודעה על כשל באימות
                                        }

                                        // בדיקה האם התרחשה שגיאת רשת
                                        else if (exp instanceof FirebaseNetworkException) {
                                            tv.setText("שגיאת רשת, אנא בדוק את החיבור שלך.");
                                            // הצגת הודעה על בעיית חיבור לאינטרנט
                                        }

                                        // אם השגיאה אינה מתאימה לאף אחת מהבדיקות הקודמות
                                        else {
                                            tv.setText("אירעה שגיאה. אנא נסה שוב מאוחר יותר.");
                                            // הצגת הודעת שגיאה כללית
                                        }
                                    }
                                }
                            });
                }
            }
        });

    }
};