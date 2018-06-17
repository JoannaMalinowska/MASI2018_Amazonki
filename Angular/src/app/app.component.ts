import { Component, OnInit,ViewEncapsulation } from '@angular/core';
declare var jquery:any;
declare var $ :any;


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class AppComponent implements OnInit {
  title = 'app';

  public ngOnInit()
  {
    $('#send').on('click', function(){


      var scan = $("#textarea").val();
  
      if(scan == ""){
        alert("Nic nie wpisałeś!!!");
      }else{



      var html_code_user = '<div class="chat user"><div class="user-photo"><img src="/assets/user_avatar.png" height="57" width="57"/></div><p class="message1">'+scan+'</p></div>';
      $('#hidden_string').val(scan);
      handler();
      $('.messages').append(html_code_user);
      scroll();
  
      $('textarea').val('');
      $("textarea").prop('disabled', true);
    }
    });

    function handler(){

      $('.messages').on("DOMSubtreeModified",function(){
    
          var scan = $('#hidden_string').val();
        var flag = $('#hidden_flag').val();
    
         var obj = { text: "", con_id : ""}
             obj.text = scan;
             obj.con_id  = flag;
    
    
    
          $.ajax({
          url: "http://localhost:8080/api/central",
            method: "POST",
            data: JSON.stringify(obj),
            dataType: "json",
            contentType: "application/json;charset=utf-8",
            success:function(data)
            {
    
              console.log(data.data.con_id);
              $('#hidden_flag').val(data.data.con_id);
              var answer_bot2 = data.data.text.substring(0, 4);
    
              if(answer_bot2 == "http"){
                var html_code_bot = '<div class="chat bot"><div class="user-photo"><img src="/assets/chatbot_avatar.png" height="57" width="57"/></div><p class="message1"><a href="'+data.data.text+'" target="_blank">"Click here to see the results."</a></p></div>';
                $('.messages').off();
                  $('.messages').append(html_code_bot);
                scroll();
                $("textarea").prop('disabled', false);
                $("textarea").focus();
                $("#rate").attr("hidden", false);
    
              }else{
                var html_code_bot = '<div class="chat bot"><div class="user-photo"><img src="/assets/chatbot_avatar.png" height="57" width="57"/></div><p class="message1">'+data.data.text+'</p></div>';
                $('.messages').off();
                  $('.messages').append(html_code_bot);
                scroll();
                $("textarea").prop('disabled', false);
                $("textarea").focus();
              }
    
            }
    
        });
    
    
      });
    
      }

      function scroll(){
        $('#messages').scrollTop(100000000);
      }
    
      $(document).keypress(function(e){
        if (e.which == 13){
            $("#send").click();
            }
      });
    
      $('#rate').click(function(){
    
      })


      var modal; 
      var btn ;
      var span ;

      $(document).ready(function(){
        modal = document.getElementById('rateModal');
        btn = document.getElementById("rate");
        span = document.getElementsByClassName("close")[0];
    });
    

    
      $('#rate').on('click', function(){
        modal.style.display = "block";
      });

      $('.close').first().on('click', function(){
        modal.style.display = "none";
      });

      window.onclick = function(event) {
        if (event.target == modal) {
            modal.style.display = "none";
        }
      }
    
      $('#rateBtn').click(function(){
        var usability = $('input[name="star"]:checked').val();
        var effectiveness = $('input[name="star2"]:checked').val();
    
        var rate = { usability: 0, effectiveness: 0}
             rate.usability = usability;
             rate.effectiveness  = effectiveness;
    
        $.ajax({
          url: "http://localhost:8080/api/centralUsability",
            method: "POST",
            data: JSON.stringify(rate),
            dataType: "json",
            contentType: "application/json;charset=utf-8",
            success:function(data)
            {
              modal.style.display = "none";
              var html_code_bot = '<div _ngcontent-c0 class="chat bot"><div class="user-photo"><img src="/assets/chatbot_avatar.png"/></div><p class="message1">Thank you for rating my service.</p></div>';
              $('.messages').off();
              $('.messages').append(html_code_bot);
              scroll();
            }
    
              });
      });



  }



}
