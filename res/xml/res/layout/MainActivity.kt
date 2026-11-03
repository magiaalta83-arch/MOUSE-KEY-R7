package com.seu.meumouse

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.KeyEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val CODIGO_PERMISSAO_SOBREPOSICAO = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        verificarPermissoes()
    }

    // DETECTA TECLAS PRESSIONADAS
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_W -> enviarComandoParaServico("TOQUE_CIMA")
            KeyEvent.KEYCODE_A -> enviarComandoParaServico("TOQUE_ESQUERDA")
            KeyEvent.KEYCODE_S -> enviarComandoParaServico("TOQUE_BAIXO")
            KeyEvent.KEYCODE_D -> enviarComandoParaServico("TOQUE_DIREITA")
            KeyEvent.KEYCODE_SPACE -> enviarComandoParaServico("TOQUE_ATIRAR")
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun enviarComandoParaServico(comando: String) {
        // Envia o comando para o serviço de acessibilidade
        val intent = Intent("com.seu.meumouse.COMANDO")
        intent.putExtra("comando", comando)
        sendBroadcast(intent)
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
                Toast.makeText(this, "✅ Permissões OK! Ative o serviço de Acessibilidade.", Toast.LENGTH_LONG).show()
            }
        }
    }
}
