package com.example.gameshop.fragment;

import android.os.Bundle;
import android.util.Log;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gameshop.R;
import com.example.gameshop.config.URL;
import com.example.gameshop.toast.ImageTextToast;
import com.example.gameshop.utils.RequestUtil;
import com.example.gameshop.utils.ResponseUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PackFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PackFragment extends Fragment {
    private static final String TAG = "PackFragment";
    private RecyclerView recycleGameListView;
    private RequestUtil getGameListRequest;
    private ResponseUtil getGameListResponse;
    private ImageTextToast toast;
    private final String type = "回收";

    public PackFragment() {
        // Required empty public constructor
    }

    public static PackFragment newInstance() {
        return new PackFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getGameListRequest = new RequestUtil(getActivity()).url(URL.RECYCLE_PACK_GAMES_LIST).get().setToken();
        getGameListResponse = new ResponseUtil()
                .success((msg, dataJSON) -> {
                    Log.d("recycle pack data", dataJSON);
                })
                .fail((msg, dataJSON) -> {
                    Log.w(TAG, "fail: " + msg);
                    toast.fail(msg);
                })
                .error((msg, dataJSON) -> {
                    Log.e(TAG, "error: " + msg);
                    toast.error(msg);
                });
        toast = new ImageTextToast(getActivity());
    }

    public void getGameList() {
        getGameListRequest
                .addQueryParameter("type", type)
                .then((call, response) -> {
                    getGameListResponse.setResponse(response).handle();
                })
                .error((call, e) -> {
                    toast.fail("网络异常");
                    e.printStackTrace();
                })
                .request();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pack, container, false);
        recycleGameListView = view.findViewById(R.id.recycle_pack_games);
        return view;
    }
}