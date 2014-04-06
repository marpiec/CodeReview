package pl.mpieciukiewicz.codereview.utils

import xml.Utility

/**
 * @author Marcin Pieciukiewicz
 */

object BBCodeConverter {
  def convert(input: String): String = {

    var output = Utility.escape(input)

    output = output.replaceAll("(\r\n|\r|\n|\n\r)", "<br/>")

    output = output.replaceAll("\\[b\\](.+?)\\[/b\\]", "<b>$1</b>")
    output = output.replaceAll("\\[i\\](.+?)\\[/i\\]", "<i>$1</i>")
    output = output.replaceAll("\\[u\\](.+?)\\[/u\\]", "<u>$1</u>")

    output = output.replaceAll("\\[h1\\](.+?)\\[/h1\\]", "<h1>$1</h1>")
    output = output.replaceAll("\\[h2\\](.+?)\\[/h2\\]", "<h2>$1</h2>")
    output = output.replaceAll("\\[h3\\](.+?)\\[/h3\\]", "<h3>$1</h3>")
    output = output.replaceAll("\\[h4\\](.+?)\\[/h4\\]", "<h4>$1</h4>")
    output = output.replaceAll("\\[h5\\](.+?)\\[/h5\\]", "<h5>$1</h5>")
    output = output.replaceAll("\\[h6\\](.+?)\\[/h6\\]", "<h6>$1</h6>")

    output = output.replaceAll("\\[quote\\](.+?)\\[/quote\\]", "<blockquote>$1</blockquote>")
    output = output.replaceAll("\\[p\\](.+?)\\[/p\\]", "<p>$1</p>")

    output = output.replaceAll("\\[center\\](.+?)\\[/center\\]", "<div align='center'>$1")
    output = output.replaceAll("\\[align=(.+?)\\](.+?)\\[/align\\]", "<div align='$1'>$2")
    output = output.replaceAll("\\[color=(.+?)\\](.+?)\\[/color\\]", "<span style='color:$1'>$2</span>")
    output = output.replaceAll("\\[size=(.+?)\\](.+?)\\[/size\\]", "<span style='font-size:$1'>$2</span>")
    output = output.replaceAll("\\[img\\](.+?)\\[/img\\]", "<img src='$1' />")
    output = output.replaceAll("\\[img=(.+?),(.+?)\\](.+?)\\[/img\\]", "<img width='$1' height='$2' src='$3' />")
    output = output.replaceAll("\\[url\\](.+?)\\[/url\\]", "<a href='$1'>$1</a>")
    output = output.replaceAll("\\[url=(.+?)\\](.+?)\\[/url\\]", "<a href='$1'>$2</a>")

    output = output.replaceAll("\\[ul\\](.+?)\\[/ul\\]", "<ul>$1</ul>")
    output = output.replaceAll("\\[li\\](.+?)\\[/li\\]", "<li>$1</li>")

    output
  }
}
