package com.seu.meumouse

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val CODIGO_PERMISSAO_SOBREPOSICAO = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        verificarPermissoes()
    }

    private fun verificarPermissoes() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivityForResult(intent, CODIGO_PERMISSAO_SOBREPOSICAO)
            } else {
                Toast.makeText(this, "✅ Permissões OK! Ative o serviço de Acessibilidade nas configurações.", Toast.LENGTH_LONG).show()
            }
        }
    }
}
