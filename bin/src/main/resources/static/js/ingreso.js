// Funcion de ojo para mostrar la contraseña
function mostrarPassword(){
		var cambio = document.getElementById("contraseña");
		if(cambio.type == "password"){
			cambio.type = "text";
			$('.icon').removeClass('far fa-eye').addClass('far fa-eye-slash');
		}else{
			cambio.type = "password";
			$('.icon').removeClass('far fa-eye-slash').addClass('far fa-eye');
		}
}

// Validacion de Formularios con Bootstrap
(function() {
  var formulario = document.getElementById('ingreso'),
      elementos = formulario.elements,
      alerta = document.getElementById('alerta');

  var validarId = function(e){
      if (formulario.id.value.length == 0) {
        alerta.innerHTML = '<div class="alert alert-danger"><a href="" class="close" data-dismiss="alert">&times;</a>Escribe tu Numero ID</div>';
        e.preventDefault();
        formulario.id.focus();
        formulario.id.select();
      }
  };
  var validarContraseña = function(e){
    if (formulario.contraseña.value.length == 0) {
      alerta.innerHTML = '<div class="alert alert-danger"><a href="" class="close" data-dismiss="alert">&times;</a>Escribe tu Contraseña</div>';
      e.preventDefault();
      formulario.contraseña.focus();
      formulario.contraseña.select();
    }
};

  var validar = function(e){
    validarId(e);
    validarContraseña(e);
  };
  
      formulario.addEventListener('submit', validar);
  })();

