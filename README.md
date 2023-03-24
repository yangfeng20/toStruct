        EN:

        Converts the JavaBean into a json structure for the object description,
        Usage: Move the cursor to any class name in the editor and
        right-click-->copy/paste special-->ToStruct
        Structured structures will be added to your clipboard

        CN:
        将JavaBean转换为对象描述的json结构；使用方法：将光标移动至编辑器任意类名上，右键-->copy/paste special-->ToStruct
        结构化结构将添加到您的剪切板


        simple example：
```java
                public class Address {
    
                    @ApiModelProperty("地址城市")
                    private String city;
    
                    @ApiModelProperty("地址国家")
                    private String country;
                }
```


        result:
```json
            {
              "type": "object",
              "properties": {
                "city": {
                  "type": "string",
                  "description": "地址城市"
                },
                "country": {
                  "type": "string",
                  "description": "地址国家"
                }
              }
            }

```