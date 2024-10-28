<div class="Box-sc-g0xbh4-0 QkQOb js-snippet-clipboard-copy-unpositioned" data-hpc="true"><article class="markdown-body entry-content container-lg" itemprop="text">
<div class="markdown-heading" dir="auto"><h1 tabindex="-1" class="heading-element" dir="auto" _msttexthash="241215" _msthash="273">Apache SystemDS</h1><a id="user-content-apache-systemds" class="anchor" aria-label="永久链接： Apache SystemDS" href="#apache-systemds" _mstaria-label="566540" _msthash="274"><svg class="octicon octicon-link" viewBox="0 0 16 16" version="1.1" width="16" height="16" aria-hidden="true"><path d="m7.775 3.275 1.25-1.25a3.5 3.5 0 1 1 4.95 4.95l-2.5 2.5a3.5 3.5 0 0 1-4.95 0 .751.751 0 0 1 .018-1.042.751.751 0 0 1 1.042-.018 1.998 1.998 0 0 0 2.83 0l2.5-2.5a2.002 2.002 0 0 0-2.83-2.83l-1.25 1.25a.751.751 0 0 1-1.042-.018.751.751 0 0 1-.018-1.042Zm-4.69 9.64a1.998 1.998 0 0 0 2.83 0l1.25-1.25a.751.751 0 0 1 1.042.018.751.751 0 0 1 .018 1.042l-1.25 1.25a3.5 3.5 0 1 1-4.95-4.95l2.5-2.5a3.5 3.5 0 0 1 4.95 0 .751.751 0 0 1-.018 1.042.751.751 0 0 1-1.042.018 1.998 1.998 0 0 0-2.83 0l-2.5 2.5a1.998 1.998 0 0 0 0 2.83Z"></path></svg></a></div>
<p dir="auto" _msttexthash="10372340797" _msthash="275"><strong _istranslated="1">概述：</strong>SystemDS 是一个开源 ML 系统，适用于端到端数据科学生命周期，包括数据集成、清理、
和特征工程，通过高效的本地和分布式 ML 模型训练，到部署和服务。对此
最后，我们的目标是为以下几项数据科学的不同任务提供一堆具有类似 R 语法的声明性语言
生命周期，以及 （2） 具有不同专业知识的用户。这些高级脚本被编译成
本地、内存中的 CPU 和 GPU 操作，以及 Apache Spark 上的分布式操作。与现有的
系统提供同构张量或 2D 数据集，并且为了服务于整个数据科学生命周期，
底层数据模型是 DataTensors，即第一维可能具有
异构和嵌套架构。</p>
<markdown-accessiblity-table data-catalyst=""><table>
<thead>
<tr>
<th _msttexthash="6234540" _msthash="276">资源</th>
<th _msttexthash="6123858" _msthash="277">链接</th>
</tr>
</thead>
<tbody>
<tr>
<td><strong _msttexthash="11905335" _msthash="278">快速开始</strong></td>
<td><a href="https://apache.github.io/systemds/site/install.html" rel="nofollow" _msttexthash="29615651" _msthash="279">安装、快速入门和 Hello World</a></td>
</tr>
<tr>
<td><strong _msttexthash="12785175" _msthash="280">文档：</strong></td>
<td><a href="https://apache.github.io/systemds/" rel="nofollow" _msttexthash="10733346" _msthash="281">SystemDS 文档</a></td>
</tr>
<tr>
<td><strong _msttexthash="9336054" _msthash="282">Python 文档</strong></td>
<td><a href="https://apache.github.io/systemds/api/python/index.html" rel="nofollow" _msttexthash="14987115" _msthash="283">Python SystemDS 文档</a></td>
</tr>
<tr>
<td><strong _msttexthash="19699121" _msthash="284">问题跟踪器</strong></td>
<td><a href="https://issues.apache.org/jira/secure/Dashboard.jspa?selectPageId=12335852" rel="nofollow" _msttexthash="12857663" _msthash="285">Jira 仪表板</a></td>
</tr>
</tbody>
</table></markdown-accessiblity-table>
<p dir="auto" _msttexthash="729788202" _msthash="286"><strong _istranslated="1">状态和版本：</strong>SystemDS 是从 SystemML 重命名而来的，SystemML 是一个 <strong _istranslated="1">Apache 顶级项目</strong>。
要从源码构建，请访问 <a href="https://apache.github.io/systemds/site/install.html" rel="nofollow" _istranslated="1">SystemDS 从源码安装</a></p>
<p dir="auto"><a href="https://github.com/apache/systemds/actions/workflows/build.yml"><img src="https://github.com/apache/systemds/actions/workflows/build.yml/badge.svg?branch=main" alt="建" style="max-width: 100%;" _mstalt="58799" _msthash="287"></a>
<a href="https://github.com/apache/systemds/actions/workflows/documentation.yml"><img src="https://github.com/apache/systemds/actions/workflows/documentation.yml/badge.svg?branch=main" alt="文档" style="max-width: 100%;" _mstalt="234962" _msthash="288"></a>
<a href="https://github.com/apache/systemds/actions/workflows/license.yml"><img src="https://github.com/apache/systemds/actions/workflows/license.yml/badge.svg?branch=main" alt="许可证检查" style="max-width: 100%;" _mstalt="193687" _msthash="289"></a>
<a href="https://github.com/apache/systemds/actions/workflows/javaTests.yml"><img src="https://github.com/apache/systemds/actions/workflows/javaTests.yml/badge.svg?branch=main" alt="Java 测试" style="max-width: 100%;" _mstalt="133978" _msthash="290"></a>
<a href="https://codecov.io/gh/apache/systemds" rel="nofollow"><img src="https://camo.githubusercontent.com/0ea886684f4498a4d863b593b03ea5fa4196d288995322de79e84750266d100a/68747470733a2f2f636f6465636f762e696f2f67682f6170616368652f73797374656d64732f67726170682f62616467652e7376673f746f6b656e3d3459667658387336447a" alt="Codecov 公司" data-canonical-src="https://codecov.io/gh/apache/systemds/graph/badge.svg?token=4YfvX8s6Dz" style="max-width: 100%;" _mstalt="96798" _msthash="291"></a>
<a href="https://github.com/apache/systemds/actions/workflows/python.yml"><img src="https://github.com/apache/systemds/actions/workflows/python.yml/badge.svg?branch=main" alt="Python 测试" style="max-width: 100%;" _mstalt="159120" _msthash="292"></a>
<a href="https://pepy.tech/project/systemds" rel="nofollow"><img src="https://camo.githubusercontent.com/8d5b24acbb801d127ab15548a5e36c6ec6e34e652c9d08c606d9c85ffdff5d55/68747470733a2f2f7374617469632e706570792e746563682f706572736f6e616c697a65642d62616467652f73797374656d64733f756e6974733d616262726576696174696f6e26706572696f643d746f74616c266c6566745f636f6c6f723d677265792672696768745f636f6c6f723d626c7565266c6566745f746578743d546f74616c25323050795049253230446f776e6c6f616473" alt="PyPI 总下载量" data-canonical-src="https://static.pepy.tech/personalized-badge/systemds?units=abbreviation&amp;period=total&amp;left_color=grey&amp;right_color=blue&amp;left_text=Total%20PyPI%20Downloads" style="max-width: 100%;" _mstalt="374790" _msthash="293"></a>
<a href="https://pepy.tech/project/systemds" rel="nofollow"><img src="https://camo.githubusercontent.com/79d53a2125c7706eeeb6f6e2ead89abaa50a7fb5975d90dce6cef592d6b5d3fc/68747470733a2f2f7374617469632e706570792e746563682f706572736f6e616c697a65642d62616467652f73797374656d64733f756e6974733d616262726576696174696f6e266c6566745f636f6c6f723d677265792672696768745f636f6c6f723d626c7565266c6566745f746578743d4d6f6e74686c7925323050795049253230446f776e6c6f616473" alt="每月 PyPI 下载" data-canonical-src="https://static.pepy.tech/personalized-badge/systemds?units=abbreviation&amp;left_color=grey&amp;right_color=blue&amp;left_text=Monthly%20PyPI%20Downloads" style="max-width: 100%;" _mstalt="447096" _msthash="294"></a></p>
</article></div>
