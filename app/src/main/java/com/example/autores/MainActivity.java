package com.example.autores;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class MainActivity extends FragmentActivity {

    private EditText inputBook;
    private TextView bookTitle;
    private TextView bookAuthor;
    private ImageView imgview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputBook = (EditText)findViewById(R.id.inputbook);
        bookTitle = (TextView)findViewById(R.id.bookTitle);
        bookAuthor = (TextView)findViewById(R.id.bookAuthor);
        imgview = (ImageView)findViewById(R.id.img);
        //Picasso.with(MainActivity.this).load("http://books.google.com/books/content?id=uoxUloj09YEC&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api").into(imgview);
    }

    public void searchBook(View view) {
        String searchString = inputBook.getText().toString();
        new GetBook(bookTitle,bookAuthor, imgview).execute(searchString);
    }

    public class GetBook extends AsyncTask<String,Void,String>{

        private WeakReference<TextView> mTextTitle;
        private WeakReference<TextView> mTextAuthor;
        private WeakReference<ImageView> mImgview;

        public GetBook(TextView mTextTitle, TextView mTextAuthor , ImageView iPortada) {
            this.mTextTitle = new WeakReference<>(mTextTitle);
            this.mTextAuthor = new WeakReference<>(mTextAuthor);
            this.mImgview = new WeakReference<>(iPortada);

        }


        @Override
        protected String doInBackground(String... strings) {
            return NetUtilities.getBookInfo(strings[0]);
        }

        @Override
        protected void onPostExecute(String s){
            super.onPostExecute(s);
            try{
                JSONObject jsonObject = new JSONObject(s);
                JSONArray itemsArray = jsonObject.getJSONArray("items");
                int i = 0;
                String title = null;
                String author = null;
                String imagen = null;
                while(i < itemsArray.length() && (title == null && author == null)){
                    JSONObject book = itemsArray.getJSONObject(i);
                    JSONObject volumeInfo = book.getJSONObject("volumeInfo");
                    JSONObject imagenInfo = volumeInfo.getJSONObject("imageLinks");
                    try{
                        title = volumeInfo.getString("title");
                        author = volumeInfo.getString("authors");
                        imagen = imagenInfo.getString("thumbnail");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    i++;
                }
                if(title != null && author != null || imagen !=null){
                    mTextTitle.get().setText(title);
                    mTextAuthor.get().setText(author);
                    Picasso.with(MainActivity.this).load(imagen).into(imgview);
                }else{
                    mTextTitle.get().setText("No existen resultados para la consulta");
                    mTextAuthor.get().setText("");

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}