package com.seu.meumouse

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Path
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast

class MeuServicoAcessibilidade : AccessibilityService() {

    private lateinit var windowManager: WindowManager
    private var botaoFlutuante: View? = null
    
    // POSIÇÕES DOS BOTÕES NA TELA
    private val posicaoAtirar = Triple(500f, 1500f, "🔫 Atirar")
    private val posicaoCima = Triple(500f, 800f, "⬆️ Cima")
    private val posicaoBaixo = Triple(500f, 1200f, "⬇️ Baixo")
    private val posicaoEsquerda = Triple(300f, 1000f, "⬅️ Esq")
    private val posicaoDireita = Triple(700f, 1000f, "➡️ Dir")

    // RECEPTOR DE COMANDOS DO TECLADO
    private val receptorComandos = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.getStringExtra("comando")?.let { comando ->
                when (comando) {
                    "TOQUE_ATIRAR" -> simularToque(posicaoAtirar.first, posicaoAtirar.second)
                    "TOQUE_CIMA" -> simularToque(posicaoCima.first, posicaoCima.second)
                    "TOQUE_BAIXO" -> simularToque(posicaoBaixo.first, posicaoBaixo.second)
                    "TOQUE_ESQUERDA" -> simularToque(posicaoEsquerda.first, posicaoEsquerda.second)
                    "TOQUE_DIREITA" -> simularToque(posicaoDireita.first, posicaoDireita.second)
                }
            }
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Toast.makeText(this, "🎮 Tecla do Mouse R7 ATIVADO!", Toast.LENGTH_SHORT).show()
        
        // REGISTRA O RECEPTOR DE COMANDOS
        registerReceiver(receptorComandos, IntentFilter("com.seu.meumouse.COMANDO"))
        
        criarJanelaFlutuante()
    }

    private fun criarJanelaFlutuante() {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        
        val layoutTipo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        // BOTÃO DE ATIRAR
        botaoFlutuante = LayoutInflater.from(this).inflate(R.layout.botao_flutuante, null)
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutTipo,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.TOP or Gravity.START
        params.x = posicaoAtirar.first.toInt() - 40
        params.y = posicaoAtirar.second.toInt() - 40

        windowManager.addView(botaoFlutuante, params)

        val botao = botaoFlutuante?.findViewById<Button>(R.id.botaoAcao)
        botao?.setOnClickListener {
            simularToque(posicaoAtirar.first, posicaoAtirar.second)
        }

        // PERMITE ARRASTAR O BOTÃO
        var inicialX = 0
        var inicialY = 0
        botao?.setOnTouchListener { _, evento ->
            when (evento.action) {
                android.view.MotionEvent.ACTION_DOWN -> {
                    inicialX = evento.rawX.toInt() - params.x
                    inicialY = evento.rawY.toInt() - params.y
                    true
                }
                android.view.MotionEvent.ACTION_MOVE -> {
                    params.x = evento.rawX.toInt() - inicialX
                    params.y = evento.rawY.toInt() - inicialY
                    posicaoAtirar.first = params.x.toFloat() + 40f
                    posicaoAtirar.second = params.y.toFloat() + 40f
                    windowManager.updateViewLayout(botaoFlutuante, params)
                    true
                }
                else -> false
            }
        }
    }

    // ✅ SIMULA UM TOQUE NA TELA
    fun simularToque(x: Float, y: Float) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val caminho = Path()
            caminho.moveTo(x, y)
            val gesto = GestureDescription.Builder()
                .addStroke(GestureDescription.StrokeDescription(caminho, 0, 80))
                .build()
            dispatchGesture(gesto, null, null)
        }
    }

    override fun onAccessibilityEvent(event: android.view.accessibility.AccessibilityEvent?) {}
    
    override fun onInterrupt() {
        unregisterReceiver(receptorComandos)
        if (botaoFlutuante != null) windowManager.removeView(botaoFlutuante)
    }
}
