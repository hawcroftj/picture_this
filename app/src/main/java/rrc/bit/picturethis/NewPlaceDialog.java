package rrc.bit.picturethis;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by hcroftj on 2018-03-28.
 */

public class NewPlaceDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_new_place)
                .setPositiveButton(R.string.from_address, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // launch new place
                    }
                })
                .setNegativeButton(R.string.from_map, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // launch map
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
